package com.jackson_api.JacksonApi.application.dto.response;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    UUID id;
    String name;
    String description;
    String imageUrl;
}
