package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateBrandRequest;
import com.jackson_api.JacksonApi.application.dto.response.BrandResponse;
import com.jackson_api.JacksonApi.application.service.BrandService;
import com.jackson_api.JacksonApi.presentation.response.PagedResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/brands")
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> create(@Valid @RequestBody CreateBrandRequest request) {
        BrandResponse response = brandService.createBrand(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping()
    public ResponseEntity<PagedResponse<BrandResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(PagedResponse.from(brandService.getAllBrands(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getById(@PathVariable UUID id) {
        BrandResponse response = brandService.getBrandById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BrandResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateBrandRequest request) {
        BrandResponse response = brandService.updateBrand(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        brandService.deleteBrand(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
