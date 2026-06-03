package com.jackson_api.JacksonApi.application.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBrandRequest {
    String name;
    String description;
    String logoUrl;
}
