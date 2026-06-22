package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter @Setter
public class CartResponse {
    UUID id;
    String userId;
    BigDecimal total;
    List<CartItemResponse> items;
}
