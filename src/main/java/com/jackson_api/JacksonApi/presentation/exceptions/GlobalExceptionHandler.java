package com.jackson_api.JacksonApi.presentation.exceptions;

import com.jackson_api.JacksonApi.domain.exceptions.ResourceNotFoundException;
import com.jackson_api.JacksonApi.presentation.response.GlobalExceptionHandlerResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//CLASE PARA INTERCEPTAR EXCEPCIONES
@RestControllerAdvice
public class GlobalExceptionHandler {

    //METODO QUE SE ACTIVA ESPECIFICAMENTE PARA ESTA CLASE NOTFOUND
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GlobalExceptionHandlerResponse> handleResourceNotFound
    (ResourceNotFoundException ex, HttpServletRequest request) {
        GlobalExceptionHandlerResponse exResponse = new GlobalExceptionHandlerResponse(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<>(exResponse, HttpStatus.NOT_FOUND);
    }

    //ERRORES DE VALIDACION DE CAMPOS ESPECIFICAMENTE
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalExceptionHandlerResponse> handleArgumentNotValid
            (MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> errors = new HashMap<>();

        //RECORRER CADA ERROR
        ex.getBindingResult().getAllErrors().forEach(e -> {
            String field = ((FieldError) e).getField();
            String message = e.getDefaultMessage();
            errors.put(field, message);
        });

        GlobalExceptionHandlerResponse response = new GlobalExceptionHandlerResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Error de Validación",
                "Uno o mas campos no cumplen con las validaciones requeridas",
                request.getRequestURI(),
                errors

        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalExceptionHandlerResponse> handleIllegalArgument
            (IllegalArgumentException ex, HttpServletRequest request) {
        GlobalExceptionHandlerResponse response = new GlobalExceptionHandlerResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Parámetro inválido",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<GlobalExceptionHandlerResponse> handleRuntime
            (RuntimeException ex, HttpServletRequest request) {
        GlobalExceptionHandlerResponse response = new GlobalExceptionHandlerResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Error interno del servidor",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
