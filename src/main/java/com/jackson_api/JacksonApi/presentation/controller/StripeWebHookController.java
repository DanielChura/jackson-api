package com.jackson_api.JacksonApi.presentation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.jackson_api.JacksonApi.application.service.PaymentService;
import com.jackson_api.JacksonApi.domain.entity.Payment;
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

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;// <- VERIFICAR QUE STRIPE ENVIO ESTE WEBHOOK

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signHeader) {

        Event event;
        try {
            event = Webhook.constructEvent(payload, signHeader, webhookSecret);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma inválida");
        }

        switch (event.getType()) {
            case "checkout.session.completed":
                Session completedSession = getSessionFromEvent(event);
                processPaymentIfPending(completedSession, PaymentStatus.COMPLETED,
                        "¡Pago exitoso confirmado por Stripe para la sesión: ");
                break;

            case "checkout.session.expired":
                Session expiredSession = getSessionFromEvent(event);
                processPaymentIfPending(expiredSession, PaymentStatus.FAILED,
                        "Error al confirmar el pago por Stripe para la sesión: ");
                break;

            default:
                System.out.println("Evento no manejado: " + event.getType());
        }
        return ResponseEntity.ok("Recibido");
    }

    private void processPaymentIfPending(Session session, PaymentStatus targetStatus, String logMessage) {
        Payment payment = paymentRepository.findByTransactionId(session.getId())
                .orElseThrow(() -> new IllegalStateException("Error al obtener el payment"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            System.out.println("Webhook ignorado - pago ya procesado: " + session.getId());
            return;
        }

        paymentService.updatePaymentStatus(payment.getId(), targetStatus);
        System.out.println(logMessage + session.getId());
    }

    private Session getSessionFromEvent(Event event) {
        Session session = (Session) event.getDataObjectDeserializer().getObject()
                .orElseThrow(() -> new IllegalStateException("No se puede parsear la sesión"));
        return session;
    }

}
