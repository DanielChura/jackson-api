package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductResponse;
import com.jackson_api.JacksonApi.application.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping()
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<ProductResponse> responses = productService.getAllProducts();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping()
    public ResponseEntity<ProductResponse> create( @Valid @RequestBody CreateProductRequest product) {
        ProductResponse response = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateProductRequest product) {
        ProductResponse response = productService.updateProduct(id, product);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
