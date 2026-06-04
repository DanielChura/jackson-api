package com.jackson_api.JacksonApi.presentation.response;


import java.time.LocalDateTime;
import java.util.Map;

public record GlobalExceptionHandlerResponse(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors

) {
    public GlobalExceptionHandlerResponse
            (Integer status, String error, String message, String path, Map<String, String> errors) {
        this(LocalDateTime.now(), status, error, message, path, errors);
    }

}