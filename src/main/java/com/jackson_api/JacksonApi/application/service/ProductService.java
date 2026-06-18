package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductImageResponse;
import com.jackson_api.JacksonApi.application.dto.response.ProductResponse;
import com.jackson_api.JacksonApi.application.mapper.ProductImageMapper;
import com.jackson_api.JacksonApi.application.mapper.ProductMapper;
import com.jackson_api.JacksonApi.domain.entity.Brand;
import com.jackson_api.JacksonApi.domain.entity.Category;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.ProductImage;
import com.jackson_api.JacksonApi.domain.enums.MovementType;
import com.jackson_api.JacksonApi.domain.exceptions.ResourceNotFoundException;
import com.jackson_api.JacksonApi.domain.repository.BrandRepository;
import com.jackson_api.JacksonApi.domain.repository.CategoryRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductImageRepository;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final InventoryMovementService inventoryMovementService;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final CloudinaryService cloudinaryService;

    @Cacheable(value = "productos", key = "{#name, #category, #brand, #pageable.pageNumber, #pageable.pageSize}")
    public Page<ProductResponse> getAllProducts(String name, String category, String brand, Pageable pageable) {
        String namePattern = null;
        if (name != null && !name.isBlank()) {
            namePattern = "%" + name.toLowerCase() + "%";
        }
        return productRepository.findByFilters(namePattern, category, brand, pageable)
                .map(productMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public ProductResponse createProduct(@NonNull CreateProductRequest request, MultipartFile[] files) {

        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("El producto ya existe");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category no encontrada"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));

        Product product = productMapper.toCreate(request, category, brand);
        Product productSaved = productRepository.save(product);

        if (files != null) {
            short order = 0;
            for (MultipartFile file : files) {
                String url = cloudinaryService.uploadFile(file, "products");
                ProductImage productImage = new ProductImage();
                productImage.setProduct(productSaved);
                productImage.setUrl(url);
                productImage.setDisplayOrder(order++);
                productImageRepository.save(productImage);
            }
        }

        return productMapper.toResponse(productSaved);
    }

    @Cacheable(value = "productos", key = "#id")
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return productMapper.toResponse(product);

    }

    @CacheEvict(value = "productos", allEntries = true)
    @Transactional
    public ProductResponse updateProduct(UUID id, @NonNull CreateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria no encontrada"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));

        Short oldStock = product.getStock();
        Short newStock = request.getStock();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(newStock);
        product.setCategory(category);
        product.setBrand(brand);
        product.setSpecifications(request.getSpecifications());

        ProductResponse response = productMapper.toResponse(productRepository.save(product));

        if (!oldStock.equals(newStock)) {
            String motivo = "Ajuste manual - " + product.getName()
                    + " (de " + oldStock + " a " + newStock + ")";
            inventoryMovementService.recordMovement(
                    product, newStock, MovementType.ADJUSTMENT, motivo);
        }

        return response;
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        productRepository.delete(product);
    }

    public List<ProductImageResponse> getImagesByProductId(UUID productId) {
        return productImageRepository.findByProductId(productId).stream()
                .map(productImageMapper::toResponse).toList();
    }

    public void deleteImage(UUID imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        productImageRepository.delete(productImage);
    }


}
