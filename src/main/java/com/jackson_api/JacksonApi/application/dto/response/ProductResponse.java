package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse {

    public UUID id;
    public String name;
    public String slug;
    public String description;
    public BigDecimal price;
    public Short stock;
    public String categoryName;
    public String brandName;
    public Map<String, String> specifications;
}
