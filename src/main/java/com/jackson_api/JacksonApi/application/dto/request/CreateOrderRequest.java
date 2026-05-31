package com.jackson_api.JacksonApi.application.dto.request;

import com.jackson_api.JacksonApi.domain.entity.OrderDetail;
import com.jackson_api.JacksonApi.domain.enums.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CreateOrderRequest {
    UUID userId;
    String shippingAddress;
    String shippingReference;
}
