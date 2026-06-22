package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateCartItemRequest;
import com.jackson_api.JacksonApi.application.dto.response.CartItemResponse;
import com.jackson_api.JacksonApi.application.dto.response.CartResponse;
import com.jackson_api.JacksonApi.application.mapper.CartItemMapper;
import com.jackson_api.JacksonApi.application.mapper.CartMapper;
import com.jackson_api.JacksonApi.domain.entity.Cart;
import com.jackson_api.JacksonApi.domain.entity.CartItem;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.repository.CartItemRepository;
import com.jackson_api.JacksonApi.domain.repository.CartRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartResponse addCartItem(@NonNull CreateCartItemRequest request) {

        if (request.getQuantity() <= 0) {
            throw new RuntimeException("La cantidad debe ser mayor a 0");
        }

        Cart cart = cartRepository.findById(request.getCartId()).orElseThrow(()
                -> new RuntimeException("El carrito no existe"));
        Product product = productRepository.findById(request.getProductId()).orElseThrow(()
                -> new RuntimeException("El producto no existe"));

        CartItem existingItem = cartItemRepository.
                findByCartIdAndProductId(request.getCartId(), request.getProductId())
                .orElse(null);

        if (existingItem != null) {
            short newQuantity = (short) (existingItem.getQuantity() + request.getQuantity());
            if (product.getStock() < newQuantity) {
                throw new RuntimeException("No hay suficiente stock");
            }

            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
            return buildCartResponse(cart);
        }

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("No hay suficiente stock");
        }

        CartItem cartItem = cartItemMapper.toCreate(cart, product, request);
        cartItemRepository.save(cartItem);
        return buildCartResponse(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {
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

    public void updateCartItem(UUID id, @NonNull CreateCartItemRequest request) {

        if (request.getQuantity() != null && request.getQuantity() < 0) {
            throw new RuntimeException("La cantidad debe ser mayor o igual a 0");
        }

        CartItem cartItem = cartItemRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Item no existe"));

        if (request.getQuantity() != null && request.getQuantity() == 0) {
            deleteCartItem(id);
            return;
        }

        Product product = cartItem.getProduct();

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("No hay suficiente stock disponible");
        }

        cartItem.setQuantity(request.getQuantity());

        cartItemRepository.save(cartItem);
    }

    public void deleteCartItem(UUID id) {
        CartItem item = cartItemRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Item no existe"));
        cartItemRepository.delete(item);
    }

    public void deleteItemsByCartId(UUID id) {
        List<CartItem> items = cartItemRepository.findByCartId(id);
        cartItemRepository.deleteAll(items);
    }

    public List<CartItemResponse> getItemsByCartId(UUID cartId) {
        return cartItemRepository.findByCartId(cartId)
                .stream().map(cartItemMapper::toResponse).toList();
    }
}
