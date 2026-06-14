package com.jackson_api.JacksonApi.application.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.jackson_api.JacksonApi.application.dto.request.StripePaymentRequest;
import com.jackson_api.JacksonApi.application.dto.response.StripePaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.Mode;

import jakarta.annotation.PostConstruct;

@Service
public class StripeService {
    @Value("${stripe.api-key}")
    private String apiKey;

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public StripePaymentResponse createCheckoutSession(StripePaymentRequest request) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(request.currency())
                                                .setUnitAmount(request.amount())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(request.productName())
                                                                .build())
                                                .build())
                                .build())
                .build();

        Session session = Session.create(params);
        return new StripePaymentResponse(session.getId(), session.getUrl());

    }
}
