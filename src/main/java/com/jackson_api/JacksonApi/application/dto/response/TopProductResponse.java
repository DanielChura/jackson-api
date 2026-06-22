package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class TopProductResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    UUID productId;
    String productName;
    Long unitsSold;
    BigDecimal revenue;
}
