package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.request.CreateCartItemRequest;
import com.jackson_api.JacksonApi.application.dto.response.CartItemResponse;
import com.jackson_api.JacksonApi.domain.entity.Cart;
import com.jackson_api.JacksonApi.domain.entity.CartItem;
import com.jackson_api.JacksonApi.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CartItemMapper {
    public CartItemResponse toResponse(CartItem item){
        CartItemResponse response = new CartItemResponse();

        response.setId(item.getId());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        response.setProductId(item.getProduct().getId());
        response.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        return response;
    }

    public CartItem toCreate(Cart cart, Product product, CreateCartItemRequest request){
        CartItem item = new CartItem();

        item.setCart(cart);
        item.setQuantity(request.getQuantity());
        item.setProduct(product);
        item.setUnitPrice(product.getPrice());

        return  item;
    }
}
