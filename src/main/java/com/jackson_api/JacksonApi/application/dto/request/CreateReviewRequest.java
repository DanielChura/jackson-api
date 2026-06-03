package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateReviewRequest {

    @NotNull(message = "El usuario es requerido")
    UUID userId;

    @NotNull(message = "El producto es requerido")
    UUID productId;

    @NotNull(message = "El rating es requerido")
    @Min(value = 1, message = "El rating mínimo es 1")
    @Max(value = 5, message = "El rating máximo es 5")
    Short rating;

    String comment;
}
