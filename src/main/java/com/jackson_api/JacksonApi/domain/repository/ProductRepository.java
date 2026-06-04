package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    @Query("""
            SELECT p FROM Product p
            WHERE (:name IS NULL OR LOWER(p.name) LIKE :name)
            AND (:category IS NULL OR p.category.name = :category)
            AND (:brand IS NULL OR p.brand.name = :brand)
            """)
    Page<Product> findByFilters(@Param("name") String namePattern,
                                @Param("category") String category,
                                @Param("brand") String brand,
                                Pageable pageable);
}
