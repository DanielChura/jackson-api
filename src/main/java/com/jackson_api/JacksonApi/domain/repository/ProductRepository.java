package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    //busqueda unica
    Optional<Product> findByName(String name);

    boolean existsByName(String name);

    //busqueda parcial
    List<Product> findByNameContaining (String name);

    //buscar por la relacion category por el atributo name
    List<Product> findByCategoryName(String category);

    //lo mismo de arriba pero ordenado por...
    List<Product> findByCategoryNameOrderByPriceDesc(String category);

    List<Product> findByCategoryNameOrderByPriceAsc(String category);

    //buscar por la relacion brand por el atributo name
    List<Product> findByBrandName(String brand);
}
