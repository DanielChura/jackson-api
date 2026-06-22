package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrderByStatusResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    String status;
    Long count;
}
