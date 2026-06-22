package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FavoriteRepository extends JpaRepository<Favorite, UUID> {
    List<Favorite> findByUser_Id(UUID id);
    List<Favorite> findByUser_Email(String email);
    Optional<Favorite> findByUser_IdAndProduct_Id(UUID userId, UUID productId);
}
