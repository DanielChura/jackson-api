package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class ProductImageResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    UUID id;
    UUID productId;
    String url;
    Short displayOrder;
}
