package com.review.storereview.repository.cms;

import com.review.storereview.dao.cms.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByImageId(Long imageId);
    List<Image> findAllByReviewId(Long reviewId);
    void deleteAllByImageIdIn(Iterable<? extends Long> imageIds);
}