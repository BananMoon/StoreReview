package com.review.storereview.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
/**
 * {@Summary Review api 응답 객체 }
 * Class       : ReviewResponseDto
 * Author      : 문 윤 지
 * History     : [2022-01-23]
 */
@Getter
public class ReviewResponseDto {

    // List< [REVIEW_ID, SAID, USER_ID, STARS, CONTENT, IMG_URL, CREATED_AT, UPDATED_AT, IS_DELETE]>
    private Long reviewId;
    private String said;
    private String userId;
    private Integer stars;
    private String content;
    private List<String> imgUrl;
    private String createdAt;
    private String updatedAt;
    private Integer isDelete;

    // 기본 생성자
    public ReviewResponseDto(Long reviewId, String said, String userId, Integer stars, String content, List<String> imgUrl, String createdAt, String updatedAt,  Integer isDelete) {
        this.reviewId = reviewId;
        this.said = said;
        this.userId = userId;
        this.stars = stars;
        this.content = content;
        this.imgUrl = imgUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDelete = isDelete;
    }
}
