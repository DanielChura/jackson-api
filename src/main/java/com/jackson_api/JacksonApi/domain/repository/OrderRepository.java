package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser_Id(UUID id);

    @Query(value = "SELECT MAX(CAST(SUBSTRING(order_number, 5) AS INTEGER)) FROM orders", nativeQuery = true)
    Integer findMaxOrderNumberSuffix();
}
