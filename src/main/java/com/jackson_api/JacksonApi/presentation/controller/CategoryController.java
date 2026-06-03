package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateCategoryRequest;
import com.jackson_api.JacksonApi.application.dto.response.CategoryResponse;
import com.jackson_api.JacksonApi.application.service.CategoryService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getById(@PathVariable UUID id) {
        CategoryResponse response = categoryService.getCategoryById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {
        List<CategoryResponse> responses = categoryService.getAllCategories();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.updateCategory(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
