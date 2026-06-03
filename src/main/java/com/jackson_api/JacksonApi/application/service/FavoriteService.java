package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateFavoriteRequest;
import com.jackson_api.JacksonApi.application.dto.response.FavoriteResponse;
import com.jackson_api.JacksonApi.application.mapper.FavoriteMapper;
import com.jackson_api.JacksonApi.application.dto.request.CreateFavoriteRequest;
import com.jackson_api.JacksonApi.application.dto.response.FavoriteResponse;
import com.jackson_api.JacksonApi.application.mapper.FavoriteMapper;
import com.jackson_api.JacksonApi.domain.entity.Favorite;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.User;
import com.jackson_api.JacksonApi.domain.repository.FavoriteRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import com.jackson_api.JacksonApi.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteMapper favoriteMapper;
    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<FavoriteResponse> findAllFavorites() {
        return favoriteMapper.toResponses(favoriteRepository.findAll());
    }

    public List<FavoriteResponse> findFavoritesByUser(UUID id) {
        return favoriteMapper.toResponses(favoriteRepository.findByUser_Id(id));
    }

    @Transactional
    public FavoriteResponse addFavorite(CreateFavoriteRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (favoriteRepository.findByUser_IdAndProduct_Id(user.getId(), product.getId()).isPresent()) {
            throw new RuntimeException("El producto ya esta en favoritos");
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setAddedAt(LocalDateTime.now());

        return favoriteMapper.toResponse(favoriteRepository.save(favorite));
    }

    public void deleteFavorite(UUID id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        favoriteRepository.delete(favorite);
    }
}
