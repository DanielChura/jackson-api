package com.jackson_api.JacksonApi.application.dto.request;

import java.util.UUID;

public record StripePaymentRequest(
        UUID orderId) {

}
