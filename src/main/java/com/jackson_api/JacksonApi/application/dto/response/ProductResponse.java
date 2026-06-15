package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class ProductResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    UUID id;
    String name;
    String slug;
    String description;
    BigDecimal price;
    Short stock;
    String categoryName;
    String brandName;
    Map<String, Object> specifications;
}
