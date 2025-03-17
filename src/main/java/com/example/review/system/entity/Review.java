package com.example.review.system.entity;

import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;
import java.time.LocalDateTime;


@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "reviews")
public class Review {
    private String id;
    private String movieId;
    private String userId;
    private int rating;
    private String comment;

}

