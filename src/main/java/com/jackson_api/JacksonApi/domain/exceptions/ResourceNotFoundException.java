package com.jackson_api.JacksonApi.domain.exceptions;

public class ResourceNotFoundException extends  RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
