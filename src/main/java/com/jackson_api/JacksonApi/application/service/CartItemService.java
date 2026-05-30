package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateCartItemRequest;
import com.jackson_api.JacksonApi.application.dto.response.CartItemResponse;
import com.jackson_api.JacksonApi.application.mapper.CartItemMapper;
import com.jackson_api.JacksonApi.domain.entity.Cart;
import com.jackson_api.JacksonApi.domain.entity.CartItem;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.repository.CartItemRepository;
import com.jackson_api.JacksonApi.domain.repository.CartRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemMapper cartItemMapper;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartItemResponse addCartItem(@NonNull CreateCartItemRequest request) {

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
            return cartItemMapper.toResponse(cartItemRepository.save(existingItem));
        }

        if (product.getStock() < request.getQuantity()) {
            throw new RuntimeException("No hay suficiente stock");
        }

        CartItem cartItem = cartItemMapper.toCreate(cart, product, request);
        return cartItemMapper.toResponse(cartItemRepository.save(cartItem));
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
