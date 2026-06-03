package com.jackson_api.JacksonApi.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateReviewRequest {

    @Min(value = 1, message = "El rating mínimo es 1")
    @Max(value = 5, message = "El rating máximo es 5")
    Short rating;

    String comment;
}
