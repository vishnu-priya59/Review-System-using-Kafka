package com.example.review.system.repo;

import com.example.review.system.entity.Review;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Repository
public class ReviewRepository {
    private final RestHighLevelClient esClient;
    private final ObjectMapper objectMapper;

    @Autowired
    public ReviewRepository(RestHighLevelClient esClient, ObjectMapper objectMapper) {
        this.esClient = esClient;
        this.objectMapper = objectMapper;
    }


    public Optional<Review> findById(String reviewId) {
        GetRequest getRequest = new GetRequest("reviews", reviewId);

        try {
            GetResponse getResponse = esClient.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                Map<String, Object> sourceMap = getResponse.getSource();
                Review review = objectMapper.convertValue(sourceMap, Review.class);
                review.setId(getResponse.getId());
                return Optional.of(review);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error retrieving review from Elasticsearch", e);
        }
    }

    public void delete(Review review) {
        DeleteRequest deleteRequest = new DeleteRequest("reviews", review.getId());

        try {
            esClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException("Error deleting review from Elasticsearch", e);
        }
    }

    public Review update(Review review) {
        UpdateRequest updateRequest = new UpdateRequest("reviews", review.getId())
                .doc(review, XContentType.JSON);

        try {
            UpdateResponse updateResponse = esClient.update(updateRequest, RequestOptions.DEFAULT);
            review.setId(updateResponse.getId());
            return review;
        } catch (IOException e) {
            throw new RuntimeException("Error updating review in Elasticsearch", e);
        }
    }


    public Review save(Review review) {
        IndexRequest indexRequest = new IndexRequest("reviews")
                .id(review.getId())
                .source(review, XContentType.JSON);

        try {
            IndexResponse indexResponse = esClient.index(indexRequest, RequestOptions.DEFAULT);
            review.setId(indexResponse.getId());
            return review;
        } catch (IOException e) {
            throw new RuntimeException("Error saving review to Elasticsearch", e);
        }
    }

}
