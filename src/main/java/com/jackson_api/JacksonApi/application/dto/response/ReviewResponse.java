package com.jackson_api.JacksonApi.application.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ReviewResponse {
    UUID id;
    UUID userId;
    UUID productId;
    String userName;
    Short rating;
    String comment;
    LocalDateTime createdAt;
}
