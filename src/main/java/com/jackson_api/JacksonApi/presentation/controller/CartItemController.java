package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateCartItemRequest;
import com.jackson_api.JacksonApi.application.dto.response.CartItemResponse;
import com.jackson_api.JacksonApi.application.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart-items")
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @GetMapping("/cart/{cartId}")
    public ResponseEntity<List<CartItemResponse>> getByCartId(@PathVariable UUID cartId) {
        return ResponseEntity.ok(cartItemService.getItemsByCartId(cartId));
    }

    @PostMapping()
    public ResponseEntity<CartItemResponse> create(@Valid @RequestBody CreateCartItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.addCartItem(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItemResponse> update(@PathVariable UUID id,
            @Valid @RequestBody CreateCartItemRequest request) {
        cartItemService.updateCartItem(id, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable UUID id) {
        cartItemService.deleteCartItem(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/cart/{cartId}")
    public ResponseEntity<?> deleteByCartId(@PathVariable UUID cartId) {
        cartItemService.deleteItemsByCartId(cartId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
