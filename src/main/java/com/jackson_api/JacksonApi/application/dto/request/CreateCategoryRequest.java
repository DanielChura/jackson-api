package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class CreateCategoryRequest {

    @NotBlank(message = "El nombre es requerido")
    String name;

    String description;

    Optional<String> imageUrl;
}
