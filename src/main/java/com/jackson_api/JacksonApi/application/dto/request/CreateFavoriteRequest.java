package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter @Setter
public class CreateFavoriteRequest {

    @NotNull(message = "El usuario es requerido")
    UUID userId;

    @NotNull(message = "El producto es requerido")
    UUID productId;
}
