package com.example.review.system.controller;

import com.example.review.system.entity.Review;
import com.example.review.system.kafka.ReviewEventProducer;
import com.example.review.system.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewEventProducer reviewEventProducer;

    @Autowired
    public ReviewController(ReviewService reviewService, ReviewEventProducer reviewEventProducer) {
        this.reviewService = reviewService;
        this.reviewEventProducer = reviewEventProducer;
    }

    @PostMapping("/{create}")
    public ResponseEntity<Review> createReview(@RequestBody Review review) {
        Review createdReview = reviewService.createReview(review);
        reviewEventProducer.publishReviewEvent(createdReview, "created");
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Review> updateReview(@PathVariable String reviewId, @RequestBody Review review) {
        Review updatedReview = reviewService.updateReview(reviewId, review);
        reviewEventProducer.publishReviewEvent(updatedReview, "updated");
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String reviewId) {
        reviewService.deleteReview(reviewId);
        //reviewEventProducer.publishReviewEvent(deletedReview, "deleted");
        return ResponseEntity.noContent().build();
    }
}
