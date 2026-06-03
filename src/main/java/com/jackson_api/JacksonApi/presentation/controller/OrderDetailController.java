package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateOrderDetailRequest;
import com.jackson_api.JacksonApi.application.dto.request.CreateOrderRequest;
import com.jackson_api.JacksonApi.application.dto.response.OrderDetailResponse;
import com.jackson_api.JacksonApi.application.dto.response.OrderResponse;
import com.jackson_api.JacksonApi.application.service.OrderDetailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @GetMapping
    public ResponseEntity<List<OrderDetailResponse>> findAll(){
        return ResponseEntity.status(HttpStatus.OK).body(orderDetailService.findAllDetails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> findById(@PathVariable UUID id){
        return ResponseEntity.status(HttpStatus.OK)
                .body(orderDetailService.findDetailById(id));
    }

    @PostMapping("/order/{id}")
    public ResponseEntity<OrderResponse> add(@PathVariable UUID id, @Valid @RequestBody List<CreateOrderDetailRequest> request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderDetailService.addOrderDetails(id,request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable UUID id){
        orderDetailService.deleteDetailById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
