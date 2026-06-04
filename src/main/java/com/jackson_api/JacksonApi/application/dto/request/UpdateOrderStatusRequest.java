package com.jackson_api.JacksonApi.application.dto.request;

import com.jackson_api.JacksonApi.domain.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrderStatusRequest {

    @NotNull(message = "El estado es requerido")
    private OrderStatus status;
}
