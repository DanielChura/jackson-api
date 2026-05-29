package com.jackson_api.JacksonApi.application.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Setter
@Getter
public class CreateProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Short stock;
    private UUID categoryId;
    private UUID brandId;
    private Map<String, String> specifications;
}
