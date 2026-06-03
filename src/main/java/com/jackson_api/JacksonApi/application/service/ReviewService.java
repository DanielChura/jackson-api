package com.jackson_api.JacksonApi.application.service;

import com.jackson_api.JacksonApi.application.dto.request.CreateReviewRequest;
import com.jackson_api.JacksonApi.application.dto.request.UpdateReviewRequest;
import com.jackson_api.JacksonApi.application.dto.response.ReviewResponse;
import com.jackson_api.JacksonApi.application.mapper.ReviewMapper;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.Review;
import com.jackson_api.JacksonApi.domain.entity.User;
import com.jackson_api.JacksonApi.domain.repository.ProductRepository;
import com.jackson_api.JacksonApi.domain.repository.ReviewRepository;
import com.jackson_api.JacksonApi.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewResponse> getAllReviews() {
        return reviewMapper.toResponseList(reviewRepository.findAll());
    }

    public ReviewResponse getReviewById(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resena no encontrada"));
        return reviewMapper.toResponse(review);
    }

    public List<ReviewResponse> getReviewsByProduct(UUID productId) {
        return reviewMapper.toResponseList(reviewRepository.findByProduct_Id(productId));
    }

    public List<ReviewResponse> getReviewsByUser(UUID userId) {
        return reviewMapper.toResponseList(reviewRepository.findByUser_Id(userId));
    }

    @Transactional
    public ReviewResponse createReview(CreateReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new RuntimeException("El rating debe estar entre 1 y 5");
        }

        if (reviewRepository.findByUser_IdAndProduct_Id(user.getId(), product.getId()).isPresent()) {
            throw new RuntimeException("Ya has resenado este producto");
        }

        Review review = reviewMapper.toCreate(request, user, product);
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponse updateReview(UUID id, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resena no encontrada"));

        if (request.getRating() != null && (request.getRating() < 1 || request.getRating() > 5)) {
            throw new RuntimeException("El rating debe estar entre 1 y 5");
        }

        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resena no encontrada"));
        reviewRepository.delete(review);
    }
}
