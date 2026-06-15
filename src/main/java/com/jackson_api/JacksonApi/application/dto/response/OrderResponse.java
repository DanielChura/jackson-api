package com.jackson_api.JacksonApi.application.dto.response;

import com.jackson_api.JacksonApi.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class OrderResponse {
    UUID id;
    UUID userId;
    String orderNumber;
    BigDecimal subtotal;
    BigDecimal taxes;
    BigDecimal total;
    String shippingAddress;
    String shippingReference;
    OrderStatus status;
    LocalDateTime orderedAt;
    List<OrderDetailResponse> items;
}
