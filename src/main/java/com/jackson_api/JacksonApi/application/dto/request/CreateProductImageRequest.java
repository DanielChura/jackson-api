package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductImageRequest {

    @NotBlank(message = "La URL de la imagen es requerida")
    String url;

    @NotNull(message = "El orden de visualización es requerido")
    @PositiveOrZero(message = "El orden no puede ser negativo")
    Short displayOrder;
}
