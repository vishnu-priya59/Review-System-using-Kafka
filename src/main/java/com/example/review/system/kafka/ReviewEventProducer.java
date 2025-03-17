package com.example.review.system.kafka;

import com.example.review.system.entity.Review;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ReviewEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public ReviewEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishReviewEvent(Review review, String eventType) {
        String reviewEvent = String.format("{\"movieId\":\"%s\",\"userId\":\"%s\",\"eventType\":\"%s\",\"rating\":%d,\"comment\":\"%s\"}",
                review.getMovieId(), review.getUserId(), eventType, review.getRating(), review.getComment());
        kafkaTemplate.send("review-events-topic", reviewEvent);
    }
}
