package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdWithLock(@Param("id") UUID id);

    boolean existsByName(String name);

    @Query("""
            SELECT p FROM Product p
            WHERE (:name IS NULL OR LOWER(p.name) LIKE :name)
            AND (:category IS NULL OR p.category.name = :category)
            AND (:brand IS NULL OR p.brand.name = :brand)
            AND p.isActive = true
            AND p.stock > 0
            """)
    Page<Product> findByFilters(@Param("name") String namePattern,
            @Param("category") String category,
            @Param("brand") String brand,
            Pageable pageable);

    @Query(value = """
            SELECT p.id, p.name,
                   COALESCE(SUM(od.quantity), 0) AS units_sold,
                   COALESCE(SUM(od.subtotal), 0) AS revenue
            FROM products p
            JOIN order_details od ON od.product_id = p.id
            JOIN orders o ON o.id = od.order_id
            WHERE o.ordered_at BETWEEN :desde AND :hasta
              AND o.status != 'CANCELLED'
            GROUP BY p.id, p.name
            ORDER BY units_sold DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findPopularProducts(@Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("limit") int limit);

    Long countByStockLessThanEqualAndIsActiveTrue(Short threshold);

    @Query(value = """
            SELECT p.id, p.name, p.stock
            FROM products p
            WHERE p.is_active = true AND p.stock <= :threshold
            ORDER BY p.stock ASC
            """, nativeQuery = true)
    List<Object[]> findLowStockProducts(@Param("threshold") short threshold);

    @Query("""
            SELECT p FROM Product p
            JOIN Favorite f ON f.product = p
            WHERE p.isActive = true
            GROUP BY p.id
            ORDER BY COUNT(f) DESC
            """)
    Page<Product> findMostFavorited(Pageable pageable);

    @Query(value = """
            WITH popular AS (
                SELECT od.product_id AS pid, COUNT(od.id) AS total_orders
                FROM order_details od
                JOIN orders o ON o.id = od.order_id
                WHERE o.status != 'CANCELLED'
                GROUP BY od.product_id
            )
            SELECT p.* FROM products p
            LEFT JOIN popular pop ON pop.pid = p.id
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN brands b ON p.brand_id = b.id
            WHERE (:name IS NULL OR LOWER(p.name) LIKE :name)
            AND (:category IS NULL OR c.name = :category)
            AND (:brand IS NULL OR b.name = :brand)
            AND p.is_active = true
            AND p.stock > 0
            ORDER BY COALESCE(pop.total_orders, 0) DESC
            """, countQuery = """
            SELECT COUNT(DISTINCT p.id) FROM products p
            LEFT JOIN categories c ON p.category_id = c.id
            LEFT JOIN brands b ON p.brand_id = b.id
            WHERE (:name IS NULL OR LOWER(p.name) LIKE :name)
            AND (:category IS NULL OR c.name = :category)
            AND (:brand IS NULL OR b.name = :brand)
            AND p.is_active = true
            AND p.stock > 0
            """, nativeQuery = true)
    Page<Product> findByFiltersOrderByPopularity(
            @Param("name") String namePattern,
            @Param("category") String category,
            @Param("brand") String brand,
            Pageable pageable);
}
