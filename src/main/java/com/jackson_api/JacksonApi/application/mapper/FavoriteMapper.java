package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.request.CreateFavoriteRequest;
import com.jackson_api.JacksonApi.application.dto.response.FavoriteResponse;
import com.jackson_api.JacksonApi.domain.entity.Favorite;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FavoriteMapper {
    public List<FavoriteResponse> toResponses(List<Favorite> favorites) {
        return favorites.stream().map(f -> {
            FavoriteResponse response = new FavoriteResponse();

            response.setId(f.getId());
            response.setProductId(f.getProduct().getId());
            response.setUserId(f.getUser().getId());

            return response;
        }).toList();
    }

    public FavoriteResponse toResponse(Favorite favorite) {
        FavoriteResponse response = new FavoriteResponse();

        response.setId(favorite.getId());
        response.setUserId(favorite.getUser().getId());
        response.setProductId(favorite.getProduct().getId());

        return response;
    }

    public Favorite toCreate(CreateFavoriteRequest request, Product product, User user) {
        Favorite favorite = new Favorite();

        favorite.setProduct(product);
        favorite.setUser(user);

        return favorite;
    }
}
