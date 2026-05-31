package com.jackson_api.JacksonApi.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateOrderDetailRequest {
    UUID productId;
    Short quantity;
}
