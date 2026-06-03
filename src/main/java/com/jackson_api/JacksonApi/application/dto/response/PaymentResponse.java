package com.jackson_api.JacksonApi.application.dto.response;

import com.jackson_api.JacksonApi.domain.enums.PaymentMethod;
import com.jackson_api.JacksonApi.domain.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponse {
    UUID id;
    UUID orderId;
    PaymentMethod paymentMethod;
    BigDecimal amount;
    PaymentStatus status;
    UUID transactionId;
    LocalDateTime paidAt;
}