package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class CartItemResponse {
    UUID id;
    UUID productId;
    Short quantity;
    BigDecimal unitPrice;
    BigDecimal subtotal;
}
