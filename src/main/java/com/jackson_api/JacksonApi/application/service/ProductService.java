package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductResponse;
import com.jackson_api.JacksonApi.application.mapper.ProductMapper;
import com.jackson_api.JacksonApi.domain.entity.Brand;
import com.jackson_api.JacksonApi.domain.entity.Category;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.repository.BrandRepository;
import com.jackson_api.JacksonApi.domain.repository.CategoryRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;

    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponse> responses = new ArrayList<>();

        for (Product product : products) {
            responses.add(productMapper.toResponse(product));
        }

        return responses;
    }

    public ProductResponse createProduct(@NonNull CreateProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("El producto ya existe");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        Product product = productMapper.toCreate(request, category, brand);
        Product productSaved = productRepository.save(product);

        return productMapper.toResponse(productSaved);
    }

    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return productMapper.toResponse(product);

    }

    public ProductResponse updateProduct(UUID id, CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new RuntimeException("Brand not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setCategory(category);
        product.setBrand(brand);
        product.setSpecifications(request.getSpecifications());

        return productMapper.toResponse(productRepository.save(product));
    }

    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        productRepository.delete(product);
    }
}
