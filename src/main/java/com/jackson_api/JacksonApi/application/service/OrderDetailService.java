package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateOrderDetailRequest;
import com.jackson_api.JacksonApi.application.dto.response.OrderResponse;
import com.jackson_api.JacksonApi.application.mapper.OrderMapper;
import com.jackson_api.JacksonApi.domain.entity.Order;
import com.jackson_api.JacksonApi.domain.entity.OrderDetail;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.repository.OrderDetailRepository;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
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
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    public OrderResponse addOrderDetails(UUID orderId, List<CreateOrderDetailRequest> requests) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order no existe"));

        for (CreateOrderDetailRequest req : requests) {
            Product product = productRepository.findById(req.getProductId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

            Short quantity = req.getQuantity();
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
        }
        return updateOrder(orderId);
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
