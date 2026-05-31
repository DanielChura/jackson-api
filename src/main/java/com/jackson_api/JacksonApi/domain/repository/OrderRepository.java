package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser_Id(UUID id);
}
