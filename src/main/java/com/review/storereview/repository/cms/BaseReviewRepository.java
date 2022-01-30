package com.review.storereview.repository.cms;

import com.review.storereview.dao.cms.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BaseReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByPlaceIdOrderByCreatedAtDesc(String placeId);

    Review findByReviewId(Long reviewId);
    void deleteByReviewId(Long reviewId);
}
