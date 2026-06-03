package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBrandRequest {

    @NotBlank(message = "El nombre es requerido")
    String name;

    String description;

    String logoUrl;
}
