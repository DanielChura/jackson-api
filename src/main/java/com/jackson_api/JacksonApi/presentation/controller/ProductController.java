package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductResponse;
import com.jackson_api.JacksonApi.application.service.ProductService;
import com.jackson_api.JacksonApi.presentation.response.PagedResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping()
    public ResponseEntity<PagedResponse<ProductResponse>> getAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @PageableDefault(page = 0, size = 20)
            @SortDefault(sort = "price", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(PagedResponse.from(productService.getAllProducts(name, category, brand, pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable UUID id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductResponse>> createBulk(@Valid @RequestBody List<@Valid CreateProductRequest> products) {
        List<ProductResponse> responses = productService.createProductsBulk(products);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest product) {
        ProductResponse response = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateProductRequest product) {
        ProductResponse response = productService.updateProduct(id, product);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
