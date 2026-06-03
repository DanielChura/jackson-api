package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class OrderDetailResponse {
    UUID id;
    UUID orderId;
    UUID productId;
    String productName;
    Short quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
}
