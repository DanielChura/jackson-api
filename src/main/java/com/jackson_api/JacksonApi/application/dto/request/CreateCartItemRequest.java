package com.jackson_api.JacksonApi.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
public class CreateCartItemRequest {
    UUID cartId;
    UUID productId;
    Short quantity;
}
