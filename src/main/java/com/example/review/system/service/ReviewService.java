package com.example.review.system.service;

import com.example.review.system.entity.Review;
import com.example.review.system.kafka.ReviewEventProducer;
import com.example.review.system.repo.ReviewRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewEventProducer reviewEventProducer;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ReviewEventProducer reviewEventProducer) {
        this.reviewRepository = reviewRepository;
        this.reviewEventProducer = reviewEventProducer;
    }

    public Review createReview(Review review) {
        Review savedReview = reviewRepository.save(review);
        reviewEventProducer.publishReviewEvent(savedReview, "created");
        return savedReview;
    }

    public Review updateReview(String reviewId, Review updatedReview) {
        Review existingReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        existingReview.setRating(updatedReview.getRating());
        existingReview.setComment(updatedReview.getComment());
        Review savedReview = reviewRepository.save(existingReview);
        reviewEventProducer.publishReviewEvent(savedReview, "updated");
        return savedReview;
    }

    public void deleteReview(String reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        reviewRepository.delete(review);
        reviewEventProducer.publishReviewEvent(review, "deleted");
    }
}

