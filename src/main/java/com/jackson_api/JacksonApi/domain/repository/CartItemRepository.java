package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    public List<CartItem> findByCartId(UUID id);
    public Optional<CartItem> findByCartIdAndProductId(UUID id, UUID productId);
}
