package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class DashboardSummaryResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    BigDecimal totalSales;
    Long totalOrders;
    BigDecimal averageTicket;
    Long totalCustomers;
    List<LowStockProductResponse> lowStockProducts;
}
