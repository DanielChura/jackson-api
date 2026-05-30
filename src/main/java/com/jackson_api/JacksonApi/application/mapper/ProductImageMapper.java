package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductImageRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductImageResponse;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.ProductImage;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper {
    public ProductImageResponse toResponse(ProductImage proImg) {
        ProductImageResponse response = new ProductImageResponse();

        response.setId(proImg.getId());
        response.setUrl(proImg.getUrl());
        response.setProductId(proImg.getProduct().getId());
        response.setDisplayOrder(proImg.getDisplayOrder());

        return response;
    }

    public ProductImage toCreate(Product product, CreateProductImageRequest request) {
        ProductImage proImg = new ProductImage();

        proImg.setProduct(product);
        proImg.setUrl(request.getUrl());
        proImg.setDisplayOrder(request.getDisplayOrder());

        return proImg;
    }
}
