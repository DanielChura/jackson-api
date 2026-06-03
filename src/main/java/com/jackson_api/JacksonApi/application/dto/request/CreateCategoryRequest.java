package com.jackson_api.JacksonApi.application.dto.request;

import java.util.Optional;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCategoryRequest {
    String name;
    String description;
    Optional<String> imageUrl;
}
