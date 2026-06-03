package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateInventoryRequest;
import com.jackson_api.JacksonApi.application.dto.response.InventoryResponse;
import com.jackson_api.JacksonApi.application.mapper.InventoryMapper;
import com.jackson_api.JacksonApi.domain.entity.InventoryMovement;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.enums.MovementType;
import com.jackson_api.JacksonApi.domain.repository.InventoryMovementRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryMovementService {

    private final InventoryMovementRepository inventoryMovementRepository;
    private final InventoryMapper inventoryMapper;
    private final ProductRepository productRepository;

    public List<InventoryResponse> findAll() {
        return inventoryMapper.toResponses(inventoryMovementRepository.findAll());
    }

    public InventoryResponse findById(UUID id) {
        return inventoryMapper.toResponse(inventoryMovementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movimiento no encontrado")));
    }

    public List<InventoryResponse> findByProductId(UUID id) {
        return inventoryMapper.toResponses(inventoryMovementRepository.findByProduct_Id(id));
    }

    @Transactional
    public InventoryResponse addMovement(CreateInventoryRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return inventoryMapper.toResponse(recordMovement(product, request.getQuantity(), request.getMovementType(), request.getReason()));
    }

    @Transactional
    public InventoryMovement recordMovement(Product product, Short quantity, MovementType type, String reason) {
        boolean needsStock = type == MovementType.OUT || type == MovementType.SALE;
        if (needsStock && product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente para " + product.getName());
        }

        Short previous = product.getStock();
        Short newStock = switch (type) {
            case IN -> (short) (previous + quantity);
            case OUT, SALE -> (short) (previous - quantity);
            case RETURN -> (short) (previous + quantity);
            case ADJUSTMENT -> quantity;
        };

        product.setStock(newStock);
        productRepository.save(product);

        InventoryMovement movement = new InventoryMovement();
        movement.setProduct(product);
        movement.setMovementType(type);
        movement.setQuantity(quantity);
        movement.setPreviousStock(previous);
        movement.setNewStock(newStock);
        movement.setReason(reason);

        return inventoryMovementRepository.save(movement);
    }
}
