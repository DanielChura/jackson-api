package com.jackson_api.JacksonApi.application.mapper;

import org.springframework.stereotype.Component;

import com.jackson_api.JacksonApi.application.dto.request.CreateCategoryRequest;
import com.jackson_api.JacksonApi.application.dto.response.CategoryResponse;
import com.jackson_api.JacksonApi.domain.entity.Category;

@Component
public class CategoryMapper {

    public Category toCreate(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl() != null ? request.getImageUrl() : "");
        return category;
    }

    public CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setImageUrl(category.getImageUrl());
        return response;
    }
}
