package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateInventoryRequest;
import com.jackson_api.JacksonApi.application.dto.response.InventoryResponse;
import com.jackson_api.JacksonApi.application.service.InventoryMovementService;
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
@RequestMapping("/inventory-movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryMovementService inventoryMovementService;

    @GetMapping
    public ResponseEntity<PagedResponse<InventoryResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(PagedResponse.from(inventoryMovementService.findAll(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryMovementService.findById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryResponse>> findByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(inventoryMovementService.findByProductId(productId));
    }

    @PostMapping
    public ResponseEntity<InventoryResponse> create(@Valid @RequestBody CreateInventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryMovementService.addMovement(request));
    }
}
