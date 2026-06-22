package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.response.DashboardSummaryResponse;
import com.jackson_api.JacksonApi.application.dto.response.OrderByStatusResponse;
import com.jackson_api.JacksonApi.application.dto.response.RecentOrderResponse;
import com.jackson_api.JacksonApi.application.dto.response.SalesByPeriodResponse;
import com.jackson_api.JacksonApi.application.dto.response.TopProductResponse;
import com.jackson_api.JacksonApi.application.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        LocalDate start = desde != null ? desde : LocalDate.now().minusDays(30);
        LocalDate end = hasta != null ? hasta : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getSummary(start, end));
    }

    @GetMapping("/sales-by-period")
    public ResponseEntity<List<SalesByPeriodResponse>> getSalesByPeriod(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(defaultValue = "day") String granularity) {
        LocalDate start = desde != null ? desde : LocalDate.now().minusDays(30);
        LocalDate end = hasta != null ? hasta : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getSalesByPeriod(start, end, granularity));
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductResponse>> getTopProducts(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta,
            @RequestParam(defaultValue = "10") int limit) {
        LocalDate start = desde != null ? desde : LocalDate.now().minusDays(30);
        LocalDate end = hasta != null ? hasta : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getTopProducts(start, end, limit));
    }

    @GetMapping("/orders-by-status")
    public ResponseEntity<List<OrderByStatusResponse>> getOrdersByStatus(
            @RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta) {
        LocalDate start = desde != null ? desde : LocalDate.now().minusDays(30);
        LocalDate end = hasta != null ? hasta : LocalDate.now();
        return ResponseEntity.ok(dashboardService.getOrdersByStatus(start, end));
    }

    @GetMapping("/recent-orders")
    public ResponseEntity<List<RecentOrderResponse>> getRecentOrders(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(dashboardService.getRecentOrders(limit));
    }
}
