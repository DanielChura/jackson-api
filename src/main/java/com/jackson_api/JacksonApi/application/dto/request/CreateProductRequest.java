package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
public class CreateProductRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 6, max = 250, message = "El nombre debe tener entre 6 y 250 caracteres")
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;

    @NotNull(message = "El precio no puede estar vacío")
    @PositiveOrZero(message = "El precio no debe ser menor a 0")
    private BigDecimal price;

    @NotNull(message = "El stock no puede estar vacío")
    @PositiveOrZero(message = "El stock no debe ser menor a 0")
    private Short stock;

    @NotNull(message = "La categoría es requerida")
    private UUID categoryId;

    @NotNull(message = "La marca es requerida")
    private UUID brandId;

    private Map<String, String> specifications;
}
