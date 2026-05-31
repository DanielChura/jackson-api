package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.response.OrderDetailResponse;
import com.jackson_api.JacksonApi.domain.entity.OrderDetail;
import org.springframework.stereotype.Component;

@Component
public class OrderDetailMapper {
    public OrderDetailResponse toResponse(OrderDetail order){
        OrderDetailResponse response = new OrderDetailResponse();

        response.setId(order.getId());
        response.setSubtotal(order.getSubtotal());
        response.setOrderId(order.getOrder().getId());
        response.setQuantity(order.getQuantity());
        response.setProductId(order.getProduct().getId());
        response.setProduct_name(order.getProductName());
        response.setUnitPrice(order.getUnitPrice());

        return response;
    }
}
