package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RecentOrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    UUID orderId;
    String orderNumber;
    String customerName;
    BigDecimal total;
    String status;
    LocalDateTime orderedAt;
}
