package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductImageRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductImageResponse;
import com.jackson_api.JacksonApi.application.mapper.ProductImageMapper;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.ProductImage;
import com.jackson_api.JacksonApi.domain.repository.ProductImageRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    public Page<ProductImageResponse> getAllImages(Pageable pageable) {
        return productImageRepository.findAll(pageable).map(productImageMapper::toResponse);
    }

    public List<ProductImageResponse> getImageByProductId(UUID id) {
        return productImageRepository.findByProductId(id).stream().map(p -> productImageMapper.toResponse(p)).toList();
    }

    public  ProductImageResponse createImage(UUID productId, CreateProductImageRequest request){
        Product product = productRepository.findById(productId).orElseThrow(()-> new RuntimeException("Producto no existe"));
        ProductImage productImage = productImageMapper.toCreate(product, request);

        return productImageMapper.toResponse(productImageRepository.save(productImage));
    }

    public void deleteImage(UUID id){
        ProductImage product = productImageRepository.findById(id).orElseThrow(()
                -> new RuntimeException("Imagen no encontrada"));

        productImageRepository.delete(product);
    }
}
