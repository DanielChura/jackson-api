package com.jackson_api.JacksonApi.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jackson_api.JacksonApi.application.dto.request.CreatePaymentRequest;
import com.jackson_api.JacksonApi.application.dto.request.StripePaymentRequest;
import com.jackson_api.JacksonApi.application.dto.response.StripePaymentResponse;
import com.jackson_api.JacksonApi.domain.entity.Order;
import com.jackson_api.JacksonApi.domain.entity.OrderDetail;
import com.jackson_api.JacksonApi.domain.enums.PaymentMethod;
import com.jackson_api.JacksonApi.domain.repository.OrderDetailRepository;
import com.jackson_api.JacksonApi.domain.repository.OrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.Mode;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StripeService {

    @Value("${stripe.api-key}")
    private String apiKey; // <- VERIFICAR MI APLICACION A STRIPE

    @Value("${stripe.success-url}")
    private String successUrl;

    @Value("${stripe.cancel-url}")
    private String cancelUrl;

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentService paymentService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    public StripePaymentResponse createCheckoutSession(StripePaymentRequest request) throws StripeException {
        Order order = orderRepository.findById(request.orderId())
                .orElseThrow(() -> new RuntimeException("Orden no encontrada"));

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder_Id(request.orderId());

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .setMode(Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl);

        for (OrderDetail detail : orderDetails) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(detail.getQuantity().longValue())
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("pen")
                                            .setUnitAmount(detail
                                                    .getUnitPrice()
                                                    .multiply(BigDecimal
                                                            .valueOf(100))
                                                    .longValue())
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData
                                                            .builder()
                                                            .setName(detail.getProductName())
                                                            .build())
                                            .build())
                            .build());
        }

        if (order.getTaxes().compareTo(BigDecimal.ZERO) > 0) {
            paramsBuilder.addLineItem(
                    SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPriceData(
                                    SessionCreateParams.LineItem.PriceData.builder()
                                            .setCurrency("pen")
                                            .setUnitAmount(order.getTaxes()
                                                    .multiply(BigDecimal
                                                            .valueOf(100))
                                                    .longValue())
                                            .setProductData(
                                                    SessionCreateParams.LineItem.PriceData.ProductData
                                                            .builder()
                                                            .setName("Impuestos "
                                                                    + order.getTaxes()
                                                                    + "%")
                                                            .build())
                                            .build())
                            .build());
        }

        Session session = Session.create(paramsBuilder.build());

        CreatePaymentRequest paymentRequest = new CreatePaymentRequest();
        paymentRequest.setAmount(order.getTotal());
        paymentRequest.setOrderId(order.getId());
        paymentRequest.setPaymentMethod(PaymentMethod.STRIPE);
        paymentRequest.setTransactionId(session.getId());

        paymentService.createPayment(paymentRequest);
        return new StripePaymentResponse(session.getId(), session.getUrl());
    }
}
