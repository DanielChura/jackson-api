package com.jackson_api.JacksonApi.application.mapper;

import org.springframework.stereotype.Component;

import com.jackson_api.JacksonApi.application.dto.request.CreateBrandRequest;
import com.jackson_api.JacksonApi.application.dto.response.BrandResponse;
import com.jackson_api.JacksonApi.domain.entity.Brand;

@Component
public class BrandMapper {

    public Brand toCreate(CreateBrandRequest request) {
        Brand brand = new Brand();
        brand.setName(request.getName());
        brand.setDescription(request.getDescription());
        brand.setLogoUrl(request.getLogoUrl());
        return brand;
    }

    public BrandResponse toResponse(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());
        response.setDescription(brand.getDescription());
        response.setLogoUrl(brand.getLogoUrl());
        return response;
    }
}
