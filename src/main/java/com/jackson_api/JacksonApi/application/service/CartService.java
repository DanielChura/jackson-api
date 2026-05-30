package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateCartRequest;
import com.jackson_api.JacksonApi.application.dto.response.CartItemResponse;
import com.jackson_api.JacksonApi.application.dto.response.CartResponse;
import com.jackson_api.JacksonApi.application.mapper.CartItemMapper;
import com.jackson_api.JacksonApi.application.mapper.CartMapper;
import com.jackson_api.JacksonApi.domain.entity.Cart;
import com.jackson_api.JacksonApi.domain.entity.User;
import com.jackson_api.JacksonApi.domain.repository.CartItemRepository;
import com.jackson_api.JacksonApi.domain.repository.CartRepository;
import com.jackson_api.JacksonApi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public List<CartResponse> getAllCart(){
        return cartRepository.findAll().stream().map(this::buildCartResponse).toList();
    }

    public CartResponse getCartById(UUID id){
        Cart cart = cartRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Carrito no encontrado"));
        return buildCartResponse(cart);
    }

    public CartResponse createCart(@NonNull CreateCartRequest request){
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("Usuario no existe"));

        if (cartRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new RuntimeException("El usuario ya tiene un carrito");
        }

        Cart cart = new Cart();
        cart.setUser(user);

        return cartMapper.toResponse(cartRepository.save(cart));
    }

    public CartResponse getCartByUserId(UUID id){
        Cart cart = cartRepository.findByUserId(id).orElseThrow(()
                -> new RuntimeException("Carrito no encontrado para el usuario"));
        return buildCartResponse(cart);
    }

    private @NonNull CartResponse buildCartResponse(Cart cart) {
        CartResponse response = cartMapper.toResponse(cart);

        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getId())
                .stream().map(cartItemMapper::toResponse).toList();
        response.setItems(items);

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotal(total);

        return response;
    }
}
