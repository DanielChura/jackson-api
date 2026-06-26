package com.jackson_api.JacksonApi.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jackson_api.JacksonApi.application.service.OrderService;
import com.jackson_api.JacksonApi.application.service.PaymentService;
import com.jackson_api.JacksonApi.domain.entity.Payment;
import com.jackson_api.JacksonApi.domain.enums.OrderStatus;
import com.jackson_api.JacksonApi.domain.enums.PaymentStatus;
import com.jackson_api.JacksonApi.domain.repository.PaymentRepository;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequestMapping("/webhooks")
@RequiredArgsConstructor
public class StripeWebHookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebHookController.class);

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final OrderService orderService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, signHeader, webhookSecret);
        } catch (Exception e) {
            log.error("Firma de webhook inválida: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida");
        }

        try {
            switch (event.getType()) {
                case "checkout.session.completed":
                    Session completedSession = getSessionFromEvent(event);
                    processPaymentIfPending(completedSession, PaymentStatus.COMPLETED,
                            "Pago exitoso confirmado por Stripe para la sesión: ",
                            OrderStatus.PAID);
                    break;

                case "checkout.session.expired":
                    Session expiredSession = getSessionFromEvent(event);
                    processPaymentIfPending(expiredSession, PaymentStatus.FAILED,
                            "Pago expirado para la sesión: ",
                            OrderStatus.CANCELLED);
                    break;

                default:
                    log.warn("Evento de Stripe no manejado: {}", event.getType());
            }
        } catch (Exception e) {
            log.error("Error procesando evento {}: {}", event.getType(), e.getMessage(), e);
        }
        return ResponseEntity.ok("Recibido");
    }

    private void processPaymentIfPending(Session session, PaymentStatus targetStatus, String logMessage,
            OrderStatus targetOrderStatus) {
        Payment payment = paymentRepository.findByTransactionId(session.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró payment para session: " + session.getId()));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            log.info("Webhook ignorado - pago ya procesado: {}", session.getId());
            return;
        }

        paymentService.updatePaymentStatus(payment.getId(), targetStatus);
        orderService.updateStatus(payment.getOrder().getId(), targetOrderStatus);
        log.info("{} {}", logMessage, session.getId());
    }

    private Session getSessionFromEvent(Event event) {
        return (Session) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new IllegalStateException("No se puede parsear la sesión"));
    }

}
