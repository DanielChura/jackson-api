package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, UUID> {

    List<Brand> findByNameContainingIgnoreCase(String name);

    List<Brand> findByIsActiveTrue();

    boolean existsByName(String name);

    List<Brand> findAllByOrderByCreatedAtAsc();
}
