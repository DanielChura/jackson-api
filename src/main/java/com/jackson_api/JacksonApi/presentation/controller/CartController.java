package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateCartRequest;
import com.jackson_api.JacksonApi.application.dto.response.CartResponse;
import com.jackson_api.JacksonApi.application.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartResponse>> getAll(){
        return  ResponseEntity.status(HttpStatus.OK).body(cartService.getAllCart());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartResponse> getById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<CartResponse> getByUserId(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(cartService.getCartByUserId(id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<CartResponse> create(@PathVariable UUID id){
        CreateCartRequest cart = new CreateCartRequest();
        cart.setUserId(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.createCart(cart));
    }
}
