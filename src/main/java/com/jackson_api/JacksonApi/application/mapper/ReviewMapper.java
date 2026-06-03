package com.jackson_api.JacksonApi.application.mapper;

import com.jackson_api.JacksonApi.application.dto.request.CreateReviewRequest;
import com.jackson_api.JacksonApi.application.dto.request.UpdateReviewRequest;
import com.jackson_api.JacksonApi.application.dto.response.ReviewResponse;
import com.jackson_api.JacksonApi.domain.entity.Product;
import com.jackson_api.JacksonApi.domain.entity.Review;
import com.jackson_api.JacksonApi.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review review) {
        ReviewResponse response = new ReviewResponse();

        response.setId(review.getId());
        response.setUserId(review.getUser().getId());
        response.setProductId(review.getProduct().getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCreatedAt(review.getCreatedAt());

        return response;
    }

    public List<ReviewResponse> toResponseList(List<Review> reviews) {
        return reviews.stream().map(this::toResponse).toList();
    }

    public Review toCreate(CreateReviewRequest request, User user, Product product) {
        Review review = new Review();

        review.setUser(user);
        review.setProduct(product);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return review;
    }
}
