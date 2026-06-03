package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateOrderDetailRequest;
import com.jackson_api.JacksonApi.application.dto.response.OrderDetailResponse;
import com.jackson_api.JacksonApi.application.dto.response.OrderResponse;
import com.jackson_api.JacksonApi.application.mapper.OrderDetailMapper;
import com.jackson_api.JacksonApi.application.mapper.OrderMapper;
import com.jackson_api.JacksonApi.domain.entity.Order;
import com.jackson_api.JacksonApi.domain.entity.OrderDetail;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.repository.OrderDetailRepository;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderDetailService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    public List<OrderDetailResponse> findAllDetails() {
        return orderDetailRepository.findAll().stream().map(orderDetailMapper::toResponse).toList();
    }

    @Transactional
    public OrderResponse addOrderDetails(UUID orderId, List<CreateOrderDetailRequest> requests) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order no existe"));

        for (CreateOrderDetailRequest req : requests) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Short quantity = req.getQuantity();

            OrderDetail existingDetail = order.getOrderDetails().stream()
                    .filter(d -> d.getProduct().getId().equals(product.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingDetail != null) {
                short newQuantity = (short) (existingDetail.getQuantity() + quantity);

                if (product.getStock() < newQuantity) {
                    throw new RuntimeException("Sin suficiente stock");
                }

                existingDetail.setQuantity(newQuantity);
                existingDetail.setSubtotal(existingDetail.getUnitPrice()
                        .multiply(BigDecimal.valueOf(newQuantity)));
                orderDetailRepository.save(existingDetail);
            } else {
                if (product.getStock() < quantity) {
                    throw new RuntimeException("Sin suficiente stock");
                }

                BigDecimal unitPrice = product.getPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProduct(product);
                detail.setQuantity(quantity);
                detail.setProductName(product.getName());
                detail.setUnitPrice(unitPrice);
                detail.setSubtotal(subtotal);
                orderDetailRepository.save(detail);

                order.getOrderDetails().add(detail);
            }
        }
        return updateOrder(orderId);
    }

    @Transactional
    public void deleteDetailById(UUID id) {
        OrderDetail detail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el detalle"));

        Order order = detail.getOrder();
        order.getOrderDetails().remove(detail);

        orderDetailRepository.delete(detail);
        updateOrder(order.getId());
    }

    public OrderDetailResponse findDetailById(UUID id) {
        return orderDetailMapper.toResponse(orderDetailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle no encontrado")));
    }

    public OrderResponse updateOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order no existe"));

        List<OrderDetail> details = order.getOrderDetails();

        BigDecimal orderSubtotal = details.stream().map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxes = orderSubtotal.multiply(new BigDecimal("0.18"));
        BigDecimal total = orderSubtotal.add(taxes);

        order.setOrderDetails(details);
        order.setTotal(total);
        order.setTaxes(taxes);
        order.setSubtotal(orderSubtotal);
        orderRepository.save(order);
        return orderMapper.toResponse(order);
    }

}
