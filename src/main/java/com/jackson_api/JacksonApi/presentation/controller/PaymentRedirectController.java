package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.domain.entity.Payment;
import com.jackson_api.JacksonApi.domain.enums.PaymentStatus;
import com.jackson_api.JacksonApi.domain.repository.PaymentRepository;
import com.jackson_api.JacksonApi.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class PaymentRedirectController {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Value("${cors.frontend:http://localhost:4200}")
    private String frontendUrl;

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId) {
        Payment payment = paymentRepository.findByTransactionId(sessionId)
                .orElse(null);

        if (payment != null && payment.getStatus() == PaymentStatus.PENDING) {
            paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.COMPLETED);
        }

        String orderId = payment != null
                ? payment.getOrder().getId().toString()
                : "";

        return "redirect:" + frontendUrl + "/checkout/success?orderId="
                + URLEncoder.encode(orderId, StandardCharsets.UTF_8);
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        return "redirect:" + frontendUrl + "/checkout/cancel";
    }
}
