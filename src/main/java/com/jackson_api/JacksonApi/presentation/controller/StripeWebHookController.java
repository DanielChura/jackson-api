package com.jackson_api.JacksonApi.presentation.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Controller
@RequestMapping("/webhooks")
public class StripeWebHookController {

    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

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
                Session session = (Session) event.getDataObjectDeserializer().getObject()
                        .orElseThrow(() -> new IllegalStateException("No se puede parsear la sesión"));
                System.out.println("¡Pago exitoso confirmado por Stripe para la sesión: " + session.getId());
            default:
                System.out.println("Evento no manejado: " + event.getType());
        }
        return ResponseEntity.ok("Recibido"); //

    }

}
