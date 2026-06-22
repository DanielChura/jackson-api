package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.response.CartResponse;
import com.jackson_api.JacksonApi.application.service.CartService;
import com.jackson_api.JacksonApi.presentation.response.PagedResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<PagedResponse<CartResponse>> getAll(Pageable pageable){
        return ResponseEntity.ok(PagedResponse.from(cartService.getAllCart(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartById(id));
    }

    @GetMapping("/mine")
    public ResponseEntity<CartResponse> getMyCart(){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartForCurrentUser());
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CartResponse> getByUserId(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartByUserId(id));
    }

    @PostMapping
    public ResponseEntity<CartResponse> create(){
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart());
    }
}
