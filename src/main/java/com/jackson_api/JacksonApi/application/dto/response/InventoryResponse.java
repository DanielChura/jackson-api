package com.jackson_api.JacksonApi.application.dto.response;

import com.jackson_api.JacksonApi.domain.enums.MovementType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class InventoryResponse {
    UUID id;
    UUID productId;
    MovementType movementType;
    Short quantity;
    Short previousStock;
    Short newStock;
    String reason;
    LocalDateTime createdAt;

}
