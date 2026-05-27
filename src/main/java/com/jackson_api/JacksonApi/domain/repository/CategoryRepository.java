package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

     List<Category> findByNameContainingIgnoreCase(String name);

     List<Category> findByIsActiveTrue();

     boolean existsByName(String name);

     List<Category> findAllByOrderByCreatedAtAsc();


}
