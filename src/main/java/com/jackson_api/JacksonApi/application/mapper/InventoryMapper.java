package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.request.CreateInventoryRequest;
import com.jackson_api.JacksonApi.application.dto.response.InventoryResponse;
import com.jackson_api.JacksonApi.domain.entity.InventoryMovement;
import com.jackson_api.JacksonApi.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryMapper {
    public InventoryResponse toResponse(InventoryMovement inventory) {
        InventoryResponse response = new InventoryResponse();

        response.setId(inventory.getId());
        response.setQuantity(inventory.getQuantity());
        response.setCreatedAt(inventory.getCreatedAt());
        response.setReason(inventory.getReason());
        response.setNewStock(inventory.getNewStock());
        response.setMovementType(inventory.getMovementType());
        response.setPreviousStock(inventory.getPreviousStock());
        response.setProductId(inventory.getProduct().getId());

        return response;
    }

    public List<InventoryResponse> toResponses(List<InventoryMovement> inventories) {
        return inventories.stream().map(this::toResponse).toList();
    }


    public InventoryMovement toCreate(CreateInventoryRequest request, Product product) {
        InventoryMovement inventoryMovement = new InventoryMovement();
        Short previous = product.getStock();
        Short quantity = request.getQuantity();
        inventoryMovement.setMovementType(request.getMovementType());
        inventoryMovement.setReason(request.getReason());
        inventoryMovement.setProduct(product);
        inventoryMovement.setQuantity(quantity);
        inventoryMovement.setPreviousStock(previous);

        switch (request.getMovementType()) {
            case IN -> {
                inventoryMovement.setNewStock((short) (previous + quantity));
            }
            case OUT -> {
                inventoryMovement.setNewStock((short) (previous - quantity));
            }
            case SALE -> {
                inventoryMovement.setNewStock((short) (previous - quantity));
            }
            case RETURN -> {
                inventoryMovement.setNewStock((short) (previous + quantity));
            }
            case ADJUSTMENT -> {
                inventoryMovement.setNewStock(quantity);
            }
        }

        return inventoryMovement;
    }

}
