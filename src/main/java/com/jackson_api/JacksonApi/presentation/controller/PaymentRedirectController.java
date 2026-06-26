package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.domain.entity.Payment;
import com.jackson_api.JacksonApi.domain.enums.PaymentStatus;
import com.jackson_api.JacksonApi.domain.repository.PaymentRepository;
import com.jackson_api.JacksonApi.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class PaymentRedirectController {

    private static final Logger log = LoggerFactory.getLogger(PaymentRedirectController.class);

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Value("${cors.frontend:http://localhost:4200}")
    private String frontendUrl;

    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam("session_id") String sessionId) {
        String orderId = "";
        boolean hasError = false;

        try {
            Payment payment = paymentRepository.findByTransactionId(sessionId)
                    .orElse(null);

            if (payment != null) {
                orderId = payment.getOrder().getId().toString();
                if (payment.getStatus() == PaymentStatus.PENDING) {
                    paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.COMPLETED);
                    log.info("Pago completado para sesión: {} - orden: {}", sessionId, orderId);
                }
            } else {
                log.warn("No se encontró pago para session_id: {}", sessionId);
            }
        } catch (Exception e) {
            log.error("Error al procesar pago exitoso para session {}: {}", sessionId, e.getMessage(), e);
            hasError = true;
        }

        String redirectUrl = frontendUrl + "/checkout/success?orderId="
                + URLEncoder.encode(orderId, StandardCharsets.UTF_8);

        if (hasError) {
            redirectUrl += "&error=" + URLEncoder.encode("El pago se procesó pero hubo un problema al actualizar tu orden. Contáctanos si el problema persiste.", StandardCharsets.UTF_8);
        }

        return "redirect:" + redirectUrl;
    }

    @GetMapping("/payment/cancel")
    public String paymentCancel() {
        log.info("Pago cancelado por el usuario");
        return "redirect:" + frontendUrl + "/checkout/cancel";
    }
}
