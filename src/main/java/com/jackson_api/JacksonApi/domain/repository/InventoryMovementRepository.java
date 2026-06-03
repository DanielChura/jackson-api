package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    public List<InventoryMovement> findByProduct_Id(UUID id);
}
