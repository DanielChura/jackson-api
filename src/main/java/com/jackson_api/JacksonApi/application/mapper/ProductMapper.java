package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductResponse;
import com.jackson_api.JacksonApi.domain.entity.Brand;
import com.jackson_api.JacksonApi.domain.entity.Category;
import com.jackson_api.JacksonApi.domain.entity.Product;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ProductMapper {
    public Product toCreate(CreateProductRequest request, Category category, Brand brand) {
        Product product = new Product();

        String slug = Arrays.stream(request.getName().split(" "))
                .map(v -> v.toLowerCase())
                .map(v -> v.trim())
                .filter(v -> !v.isEmpty())
                .reduce((a, b) -> a + "-" + b)
                .orElse("");
        product.setName(request.getName());
        product.setSlug(slug);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setBrand(brand);
        product.setSpecifications(request.getSpecifications());
        product.setIsActive(true);

        return product;
    }

    public ProductResponse toResponse(Product request) {
        ProductResponse response = new ProductResponse();

        response.setId(request.getId());
        response.setName(request.getName());
        response.setSlug(request.getSlug());
        response.setDescription(request.getDescription());
        response.setPrice(request.getPrice());
        response.setStock(request.getStock());
        response.setCategoryName(request.getCategory().getName());
        response.setBrandName(request.getBrand().getName());
        response.setSpecifications(request.getSpecifications());

        return response;
    }

    public List<ProductResponse> toResponses(List<Product> productList) {
        return productList.stream().map(this::toResponse).toList();
    }
}
