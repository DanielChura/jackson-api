package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class CategoryResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    UUID id;
    String name;
    String description;
    String imageUrl;
}
