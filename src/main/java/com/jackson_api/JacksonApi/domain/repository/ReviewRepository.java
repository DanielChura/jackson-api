package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByProduct_Id(UUID productId);
    List<Review> findByUser_Id(UUID userId);
    Optional<Review> findByUser_IdAndProduct_Id(UUID userId, UUID productId);
}
