package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.response.CartResponse;
import com.jackson_api.JacksonApi.domain.entity.Cart;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;

@Component
public class CartMapper {
    public CartResponse toResponse (Cart cart){
        CartResponse response = new CartResponse();

        response.setId(cart.getId());
        response.setUserId(cart.getUser().getEmail());
        response.setItems(Collections.emptyList());
        response.setTotal(BigDecimal.ZERO);

        return  response;
    }
}
