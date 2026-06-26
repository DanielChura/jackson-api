package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateOrderRequest;
import com.jackson_api.JacksonApi.application.dto.response.OrderResponse;
import com.jackson_api.JacksonApi.application.mapper.OrderMapper;
import com.jackson_api.JacksonApi.domain.entity.Cart;
import com.jackson_api.JacksonApi.domain.entity.CartItem;
import com.jackson_api.JacksonApi.domain.entity.Order;
import com.jackson_api.JacksonApi.domain.entity.OrderDetail;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.User;
import com.jackson_api.JacksonApi.domain.enums.MovementType;
import com.jackson_api.JacksonApi.domain.enums.OrderStatus;
import com.jackson_api.JacksonApi.domain.repository.CartItemRepository;
import com.jackson_api.JacksonApi.domain.repository.CartRepository;
import com.jackson_api.JacksonApi.domain.repository.OrderDetailRepository;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import com.jackson_api.JacksonApi.infrastructure.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final BigDecimal TAX_RATE = new BigDecimal("0.18");

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final SecurityUtil securityUtil;
    private final OrderMapper orderMapper;
    private final InventoryMovementService inventoryMovementService;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderDetailRepository orderDetailRepository;

    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable).map(orderMapper::toResponse);
    }

    public OrderResponse getOrderById(UUID id) {
        return orderMapper.toResponse(orderRepository.findById(id).orElseThrow(()
                -> new RuntimeException("No existe esta orden")));
    }

    public List<OrderResponse> getByUserId(UUID id) {
        return orderRepository.findByUser_Id(id).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public OrderResponse create(CreateOrderRequest request) {
        User user = securityUtil.getCurrentUser();

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("El usuario no tiene un carrito"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío");
        }

        Long seq = orderRepository.nextOrderNumberSeq();
        String orderNumber = "ORD-" + String.format("%04d", seq);

        BigDecimal subtotal = cartItems.stream()
                .map(i -> i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxes = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(taxes);

        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(orderNumber);
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingReference(request.getShippingReference());
        order.setSubtotal(subtotal);
        order.setTaxes(taxes);
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        List<OrderDetail> details = cartItems.stream().map(cartItem -> {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(cartItem.getProduct());
            detail.setProductName(cartItem.getProduct().getName());
            detail.setQuantity(cartItem.getQuantity());
            detail.setUnitPrice(cartItem.getUnitPrice());
            detail.setSubtotal(cartItem.getUnitPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            return detail;
        }).toList();

        orderDetailRepository.saveAll(details);
        cartItemRepository.deleteAll(cartItems);

        savedOrder.setOrderDetails(details);
        return orderMapper.toResponse(savedOrder);
    }

    @Transactional
    @CacheEvict(value = "dashboard", allEntries = true)
    public OrderResponse updateStatus(UUID id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order no existe"));

        OrderStatus oldStatus = order.getStatus();

        if (status == OrderStatus.CANCELLED && oldStatus == OrderStatus.PAID) {
            for (var detail : order.getOrderDetails()) {
                Product product = productRepository.findByIdWithLock(detail.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                product.setStock((short) (product.getStock() + detail.getQuantity()));
                productRepository.save(product);
                inventoryMovementService.recordMovement(product, detail.getQuantity(), MovementType.RETURN,
                        "Cancelacion de orden " + order.getOrderNumber());
            }
        }

        order.setStatus(status);
        return orderMapper.toResponse(orderRepository.save(order));
    }
}
