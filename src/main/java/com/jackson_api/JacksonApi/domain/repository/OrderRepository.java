package com.jackson_api.JacksonApi.domain.repository;

import com.jackson_api.JacksonApi.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser_Id(UUID id);

    @Query(value = "SELECT nextval('order_number_seq')", nativeQuery = true)
    Long nextOrderNumberSeq();

    @Query(value = """
            SELECT COALESCE(SUM(o.total), 0) AS total_sales,
                   COUNT(o.id) AS total_orders,
                   COALESCE(AVG(o.total), 0) AS average_ticket
            FROM orders o
            WHERE o.ordered_at BETWEEN :desde AND :hasta
              AND o.status != 'CANCELLED'
            """, nativeQuery = true)
    List<Object[]> findSalesSummary(@Param("desde") LocalDateTime desde,
                                    @Param("hasta") LocalDateTime hasta);

    @Query(value = """
            SELECT DATE_TRUNC(:granularity, o.ordered_at) AS period,
                   COALESCE(SUM(o.total), 0) AS total_sales,
                   COUNT(o.id) AS order_count
            FROM orders o
            WHERE o.ordered_at BETWEEN :desde AND :hasta
              AND o.status != 'CANCELLED'
            GROUP BY period
            ORDER BY period
            """, nativeQuery = true)
    List<Object[]> findSalesByPeriod(@Param("desde") LocalDateTime desde,
                                     @Param("hasta") LocalDateTime hasta,
                                     @Param("granularity") String granularity);

    @Query(value = """
            SELECT o.status, COUNT(o.id) AS count
            FROM orders o
            WHERE o.ordered_at BETWEEN :desde AND :hasta
            GROUP BY o.status
            """, nativeQuery = true)
    List<Object[]> findOrdersByStatus(@Param("desde") LocalDateTime desde,
                                      @Param("hasta") LocalDateTime hasta);

    @Query(value = """
            SELECT o.id, o.order_number,
                   CONCAT(u.first_name, ' ', u.last_name) AS customer_name,
                   o.total, o.status, o.ordered_at
            FROM orders o
            JOIN users u ON u.id = o.user_id
            ORDER BY o.ordered_at DESC
            LIMIT :limit
            """, nativeQuery = true)
    List<Object[]> findRecentOrders(@Param("limit") int limit);
}
