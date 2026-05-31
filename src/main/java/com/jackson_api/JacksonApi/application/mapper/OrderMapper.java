package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.response.OrderResponse;
import com.jackson_api.JacksonApi.domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderDetailMapper orderDetailMapper;

    public OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();

        response.setId(order.getId());
        response.setOrderedAt(order.getOrderedAt());
        response.setOrderNumber(order.getOrderNumber());
        response.setStatus(order.getStatus());
        response.setTaxes(order.getTaxes());
        response.setSubtotal(order.getSubtotal());
        response.setTotal(order.getTotal());
        response.setUserId(order.getUser().getId());
        response.setShippingAddress(order.getShippingAddress());
        response.setShippingReference(order.getShippingReference());
        response.setItems(order.getOrderDetails().stream()
                .map(orderDetailMapper::toResponse).toList());

        return response;
    }
}
