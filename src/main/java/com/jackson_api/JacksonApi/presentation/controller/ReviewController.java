package com.jackson_api.JacksonApi.presentation.controller;

import com.jackson_api.JacksonApi.application.dto.request.CreateReviewRequest;
import com.jackson_api.JacksonApi.application.dto.request.UpdateReviewRequest;
import com.jackson_api.JacksonApi.application.dto.response.ReviewResponse;
import com.jackson_api.JacksonApi.application.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getByProduct(@PathVariable UUID productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProduct(productId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getByUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
