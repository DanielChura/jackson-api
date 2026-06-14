package com.jackson_api.JacksonApi.application.dto.request;

public record StripePaymentRequest(
                String productName,
                Long amount,
                String currency) {

}
