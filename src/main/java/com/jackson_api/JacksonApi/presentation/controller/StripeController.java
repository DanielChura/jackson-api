package com.jackson_api.JacksonApi.presentation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jackson_api.JacksonApi.application.dto.request.StripePaymentRequest;
import com.jackson_api.JacksonApi.application.dto.response.StripePaymentResponse;
import com.jackson_api.JacksonApi.application.service.StripeService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class StripeController {

    private final StripeService stripeService;

    @PostMapping("/session")
    public ResponseEntity<StripePaymentResponse> createSession(@RequestBody StripePaymentRequest request) {
        try {
            StripePaymentResponse response = stripeService.createCheckoutSession(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
