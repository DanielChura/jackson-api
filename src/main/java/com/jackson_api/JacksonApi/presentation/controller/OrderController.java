package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateOrderRequest;
import com.jackson_api.JacksonApi.application.dto.request.UpdateOrderStatusRequest;
import com.jackson_api.JacksonApi.application.dto.response.OrderResponse;
import com.jackson_api.JacksonApi.application.service.OrderService;
import com.jackson_api.JacksonApi.presentation.response.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping
    public ResponseEntity<PagedResponse<OrderResponse>> getAll(Pageable pageable){
        return ResponseEntity.ok(PagedResponse.from(orderService.getAllOrders(pageable)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getOrderById(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<OrderResponse>> getByUserId(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK).body(orderService.getByUserId(id));
    }

    @PostMapping()
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateOrderStatusRequest request){

        return ResponseEntity.status(HttpStatus.OK).body(orderService.updateStatus(id, request.getStatus()));
    }
}
