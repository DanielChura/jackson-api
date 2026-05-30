package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductImageResponse {
    UUID id;
    UUID productId;
    String url;
    Short displayOrder;
}
