package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class SalesByPeriodResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    String period;
    BigDecimal totalSales;
    Long orderCount;
}
