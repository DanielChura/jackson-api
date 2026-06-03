package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateOrderDetailRequest {

    @NotNull(message = "El producto es requerido")
    UUID productId;

    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a 0")
    Short quantity;
}
