package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductImageRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductImageResponse;
import com.jackson_api.JacksonApi.application.service.ProductImageService;
import com.jackson_api.JacksonApi.presentation.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/product-images")
@RequiredArgsConstructor
public class ProductImageController {
    private final ProductImageService productImageService;

    @GetMapping()
    public ResponseEntity<PagedResponse<ProductImageResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(PagedResponse.from(productImageService.getAllImages(pageable)));
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<List<ProductImageResponse>> getByProductId(@PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(productImageService.getImageByProductId(id));
    }

    @PostMapping("/product/{id}")
    public ResponseEntity<ProductImageResponse> create(@PathVariable UUID id,
            @Valid @RequestBody CreateProductImageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productImageService.createImage(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        productImageService.deleteImage(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
