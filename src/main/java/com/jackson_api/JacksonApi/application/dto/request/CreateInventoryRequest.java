package com.jackson_api.JacksonApi.application.dto.request;

import com.jackson_api.JacksonApi.domain.enums.MovementType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateInventoryRequest {

    @NotNull(message = "El producto es requerido")
    UUID productId;

    @NotNull(message = "El tipo de movimiento es requerido")
    MovementType movementType;

    @NotNull(message = "La cantidad es requerida")
    @Positive(message = "La cantidad debe ser mayor a 0")
    Short quantity;

    @NotBlank(message = "El motivo es requerido")
    String reason;
}
