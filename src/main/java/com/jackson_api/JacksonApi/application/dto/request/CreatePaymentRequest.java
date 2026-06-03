package com.jackson_api.JacksonApi.application.dto.request;

import com.jackson_api.JacksonApi.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull(message = "La orden es requerida")
    UUID orderId;

    @NotNull(message = "El método de pago es requerido")
    PaymentMethod paymentMethod;

    @NotNull(message = "El monto es requerido")
    @Positive(message = "El monto debe ser mayor a 0")
    BigDecimal amount;
}
