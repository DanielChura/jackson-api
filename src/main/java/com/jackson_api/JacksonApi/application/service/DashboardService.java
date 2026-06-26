package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.response.DashboardSummaryResponse;
import com.jackson_api.JacksonApi.application.dto.response.LowStockProductResponse;
import com.jackson_api.JacksonApi.application.dto.response.OrderByStatusResponse;
import com.jackson_api.JacksonApi.application.dto.response.RecentOrderResponse;
import com.jackson_api.JacksonApi.application.dto.response.SalesByPeriodResponse;
import com.jackson_api.JacksonApi.application.dto.response.TopProductResponse;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final short LOW_STOCK_THRESHOLD = 3;

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Cacheable(value = "dashboard", key = "'summary:' + #desde + ':' + #hasta")
    public DashboardSummaryResponse getSummary(LocalDate desde, LocalDate hasta) {
        LocalDateTime start = toStartDateTime(desde);
        LocalDateTime end = toEndDateTime(hasta);

        List<Object[]> results = orderRepository.findSalesSummary(start, end);
        Long totalCustomers = orderRepository.countDistinctCustomersByDateRange(start, end);
        List<LowStockProductResponse> lowStock = productRepository.findLowStockProducts(LOW_STOCK_THRESHOLD)
                .stream().map(row -> {
                    LowStockProductResponse item = new LowStockProductResponse();
                    item.setProductId(toUUID(row[0]));
                    item.setProductName((String) row[1]);
                    item.setStock(((Number) row[2]).shortValue());
                    return item;
                }).toList();

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
        response.setLowStockProducts(lowStock);

        return response;
    }

    @Cacheable(value = "dashboard", key = "'salesByPeriod:' + #desde + ':' + #hasta")
    public List<SalesByPeriodResponse> getSalesByPeriod(LocalDate desde, LocalDate hasta) {
        LocalDateTime start = toStartDateTime(desde);
        LocalDateTime end = toEndDateTime(hasta);

        return orderRepository.findSalesByPeriod(start, end, "day").stream()
                .map(row -> {
                    SalesByPeriodResponse response = new SalesByPeriodResponse();
                    response.setPeriod(toLocalDateTime(row[0]).format(DateTimeFormatter.ISO_LOCAL_DATE));
                    response.setTotalSales((BigDecimal) row[1]);
                    response.setOrderCount(((Number) row[2]).longValue());
                    return response;
                })
                .toList();
    }

    @Cacheable(value = "dashboard", key = "'topProducts:' + #desde + ':' + #hasta + ':' + #limit")
    public List<TopProductResponse> getTopProducts(LocalDate desde, LocalDate hasta, int limit) {
        LocalDateTime start = toStartDateTime(desde);
        LocalDateTime end = toEndDateTime(hasta);

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
        LocalDateTime start = toStartDateTime(desde);
        LocalDateTime end = toEndDateTime(hasta);

        return orderRepository.findOrdersByStatusIncludingCancelled(start, end).stream()
                .map(row -> {
                    OrderByStatusResponse response = new OrderByStatusResponse();
                    response.setStatus(row[0].toString());
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
                    if (row[5] != null) {
                        response.setOrderedAt(toLocalDateTime(row[5]));
                    }
                    return response;
                })
                .toList();
    }

    private static LocalDateTime toStartDateTime(LocalDate date) {
        return date.atStartOfDay();
    }

    private static LocalDateTime toEndDateTime(LocalDate date) {
        return date.atTime(LocalTime.MAX);
    }

    private static LocalDateTime toLocalDateTime(Object value) {
        if (value instanceof LocalDateTime ldt) {
            return ldt;
        }
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toLocalDateTime();
        }
        return null;
    }

    private static UUID toUUID(Object value) {
        if (value instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(value.toString());
    }
}
