package com.jackson_api.JacksonApi.application.dto.response;

public record StripePaymentResponse(
        String sessionId,
        String checkoutUrl) {

}
