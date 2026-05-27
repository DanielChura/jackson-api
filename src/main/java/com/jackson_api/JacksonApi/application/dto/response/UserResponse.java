package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserResponse {
    UUID id;
    String firstName;
    String email;
    String phone;
    String address;
    String roleName;
}
