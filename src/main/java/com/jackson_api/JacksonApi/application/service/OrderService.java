package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateOrderRequest;
import com.jackson_api.JacksonApi.application.dto.response.OrderResponse;
import com.jackson_api.JacksonApi.application.mapper.OrderMapper;
import com.jackson_api.JacksonApi.domain.entity.Order;
import com.jackson_api.JacksonApi.domain.entity.User;
import com.jackson_api.JacksonApi.domain.enums.OrderStatus;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.jackson_api.JacksonApi.infrastructure.security.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final SecurityUtil securityUtil;
    private final OrderMapper orderMapper;

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

    public OrderResponse create(CreateOrderRequest request) {
        User user = securityUtil.getCurrentUser();

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.getShippingAddress());
        order.setShippingReference(request.getShippingReference());
        order.setSubtotal(BigDecimal.ZERO);
        order.setTaxes(BigDecimal.ZERO);
        order.setTotal(BigDecimal.ZERO);
        order.setStatus(OrderStatus.PENDING);

        return orderMapper.toResponse(orderRepository.save(order));
    }

    public OrderResponse updateStatus(UUID id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order no existe"));

        order.setStatus(status);
        return orderMapper.toResponse(orderRepository.save(order));
    }
}
