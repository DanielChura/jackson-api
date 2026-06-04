package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RoleResponse {
    UUID id;
    String name;
    String description;
    LocalDateTime createdAt;
}
