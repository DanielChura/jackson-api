package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateProductRequest;
import com.jackson_api.JacksonApi.application.dto.response.ProductImageResponse;
import com.jackson_api.JacksonApi.application.dto.response.ProductResponse;
import com.jackson_api.JacksonApi.application.dto.response.TopProductResponse;
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
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final InventoryMovementService inventoryMovementService;
    private final ProductMapper productMapper;
    private final ProductImageMapper productImageMapper;
    private final CloudinaryService cloudinaryService;

    @Cacheable(value = "productos", key = "{#name, #category, #brand, #sortBy, #pageable.pageNumber, #pageable.pageSize}")
    public Page<ProductResponse> getAllProducts(String name, String category, String brand, String sortBy,
            Pageable pageable) {
        String namePattern = null;
        if (name != null && !name.isBlank()) {
            namePattern = "%" + name.toLowerCase() + "%";
        }

        return switch (sortBy) {
            case "popular" -> productRepository.findByFiltersOrderByPopularity(
                    namePattern, category, brand, pageable)
                    .map(productMapper::toResponse);
            default -> {
                Sort sort = switch (sortBy) {
                    case "recent" -> Sort.by("createdAt").descending();
                    case "price-asc" -> Sort.by("price").ascending();
                    case "price-desc" -> Sort.by("price").descending();
                    case "name" -> Sort.by("name").ascending();
                    default -> Sort.by("price").descending();
                };
                Pageable sorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
                yield productRepository.findByFilters(namePattern, category, brand, sorted)
                        .map(productMapper::toResponse);
            }
        };
    }

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public ProductResponse createProduct(@NonNull CreateProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new RuntimeException("El producto ya existe");
        }
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category no encontrada"));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));

        Product product = productMapper.toCreate(request, category, brand);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Cacheable(value = "productos", key = "#id")
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return productMapper.toResponse(product);

    }

    public List<ProductResponse> getRecentProducts(int limit) {
        return productRepository.findByFilters(null, null, null,
                PageRequest.of(0, limit, Sort.by("createdAt").descending()))
                .getContent()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<TopProductResponse> getPopularProducts(int limit) {
        LocalDateTime desde = LocalDateTime.now().minusMonths(3);
        LocalDateTime hasta = LocalDateTime.now();
        return productRepository.findPopularProducts(desde, hasta, limit)
                .stream()
                .map(row -> {
                    TopProductResponse resp = new TopProductResponse();
                    resp.setProductId((UUID) row[0]);
                    resp.setProductName((String) row[1]);
                    resp.setUnitsSold(((Number) row[2]).longValue());
                    resp.setRevenue((BigDecimal) row[3]);
                    return resp;
                })
                .toList();
    }

    public List<ProductResponse> getMostFavorited(int limit) {
        return productRepository.findMostFavorited(PageRequest.of(0, limit))
                .stream()
                .map(productMapper::toResponse)
                .toList();
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
        product.setCategory(category);
        product.setBrand(brand);

        if (!oldStock.equals(newStock)) {
            String motivo = "Ajuste manual - " + product.getName()
                    + " (de " + oldStock + " a " + newStock + ")";
            inventoryMovementService.recordMovement(
                    product, newStock, MovementType.ADJUSTMENT, motivo);
        }

        productRepository.save(product);
        return productMapper.toResponse(product);
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

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public void deleteImage(UUID imageId) {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagen no encontrada"));
        productImageRepository.delete(productImage);
    }

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public ProductResponse updateSpecifications(UUID id, Map<String, Object> specifications) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        product.setSpecifications(specifications);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = "productos", allEntries = true)
    public List<ProductImageResponse> addImagesToProduct(UUID productId, MultipartFile[] files) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        short order = (short) productImageRepository.findByProductId(productId).size();
        List<ProductImage> images = new ArrayList<>();

        for (MultipartFile file : files) {
            String url = cloudinaryService.uploadFile(file, "products");
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setUrl(url);
            productImage.setDisplayOrder(order++);
            images.add(productImageRepository.save(productImage));
        }

        return images.stream().map(productImageMapper::toResponse).toList();
    }

}
