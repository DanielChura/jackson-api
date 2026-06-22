package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.response.DashboardSummaryResponse;
import com.jackson_api.JacksonApi.application.dto.response.OrderByStatusResponse;
import com.jackson_api.JacksonApi.application.dto.response.RecentOrderResponse;
import com.jackson_api.JacksonApi.application.dto.response.SalesByPeriodResponse;
import com.jackson_api.JacksonApi.application.dto.response.TopProductResponse;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import com.jackson_api.JacksonApi.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Cacheable(value = "dashboard", key = "'summary:' + #desde + ':' + #hasta")
    public DashboardSummaryResponse getSummary(LocalDate desde, LocalDate hasta) {
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime end = hasta.atTime(LocalTime.MAX);

        List<Object[]> results = orderRepository.findSalesSummary(start, end);
        Long totalCustomers = userRepository.countByRoleName("CUSTOMER");
        Long lowStockProducts = productRepository.countByStockLessThanEqualAndIsActiveTrue((short) 5);

        DashboardSummaryResponse response = new DashboardSummaryResponse();
        if (!results.isEmpty()) {
            Object[] row = results.get(0);
            response.setTotalSales((BigDecimal) row[0]);
            response.setTotalOrders(((Number) row[1]).longValue());
            response.setAverageTicket((BigDecimal) row[2]);
        } else {
            response.setTotalSales(BigDecimal.ZERO);
            response.setTotalOrders(0L);
            response.setAverageTicket(BigDecimal.ZERO);
        }
        response.setTotalCustomers(totalCustomers);
        response.setLowStockProducts(lowStockProducts);

        return response;
    }

    @Cacheable(value = "dashboard", key = "'salesByPeriod:' + #desde + ':' + #hasta + ':' + #granularity")
    public List<SalesByPeriodResponse> getSalesByPeriod(LocalDate desde, LocalDate hasta, String granularity) {
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime end = hasta.atTime(LocalTime.MAX);

        return orderRepository.findSalesByPeriod(start, end, granularity).stream()
                .map(row -> {
                    SalesByPeriodResponse response = new SalesByPeriodResponse();
                    Timestamp ts = (Timestamp) row[0];
                    response.setPeriod(ts.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE));
                    response.setTotalSales((BigDecimal) row[1]);
                    response.setOrderCount(((Number) row[2]).longValue());
                    return response;
                })
                .toList();
    }

    @Cacheable(value = "dashboard", key = "'topProducts:' + #desde + ':' + #hasta + ':' + #limit")
    public List<TopProductResponse> getTopProducts(LocalDate desde, LocalDate hasta, int limit) {
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime end = hasta.atTime(LocalTime.MAX);

        return productRepository.findTopProducts(start, end, limit).stream()
                .map(row -> {
                    TopProductResponse response = new TopProductResponse();
                    response.setProductId(toUUID(row[0]));
                    response.setProductName((String) row[1]);
                    response.setUnitsSold(((Number) row[2]).longValue());
                    response.setRevenue((BigDecimal) row[3]);
                    return response;
                })
                .toList();
    }

    @Cacheable(value = "dashboard", key = "'ordersByStatus:' + #desde + ':' + #hasta")
    public List<OrderByStatusResponse> getOrdersByStatus(LocalDate desde, LocalDate hasta) {
        LocalDateTime start = desde.atStartOfDay();
        LocalDateTime end = hasta.atTime(LocalTime.MAX);

        return orderRepository.findOrdersByStatus(start, end).stream()
                .map(row -> {
                    OrderByStatusResponse response = new OrderByStatusResponse();
                    response.setStatus((String) row[0]);
                    response.setCount(((Number) row[1]).longValue());
                    return response;
                })
                .toList();
    }

    @Cacheable(value = "dashboard", key = "'recentOrders:' + #limit")
    public List<RecentOrderResponse> getRecentOrders(int limit) {
        return orderRepository.findRecentOrders(limit).stream()
                .map(row -> {
                    RecentOrderResponse response = new RecentOrderResponse();
                    response.setOrderId(toUUID(row[0]));
                    response.setOrderNumber((String) row[1]);
                    response.setCustomerName((String) row[2]);
                    response.setTotal((BigDecimal) row[3]);
                    response.setStatus((String) row[4]);
                    response.setOrderedAt(((Timestamp) row[5]).toLocalDateTime());
                    return response;
                })
                .toList();
    }

    private UUID toUUID(Object value) {
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(value.toString());
    }
}
