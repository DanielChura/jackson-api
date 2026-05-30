package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
     List<ProductImage> findByProductId(UUID id);
}
