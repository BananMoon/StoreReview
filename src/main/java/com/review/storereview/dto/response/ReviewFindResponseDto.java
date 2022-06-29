package com.review.storereview.dto.response;

import lombok.Getter;
import java.util.List;
/**
 * {@Summary Review api findOneReview 응답 객체 }
 * Class       : ReviewFindResponseDto
 * Author      : 문 윤 지
 * History     : [2022-01-23]
 */
@Getter
public class ReviewFindResponseDto {
    // List< [REVIEW_ID, SAID, USER_ID, STARS, CONTENT, CREATED_AT, UPDATED_AT, IS_DELETE]>
    private final Long reviewId;
    private final String said;
    private final String userId;
    private final Integer stars;
    private final String content;
    private final String createdAt;
    private final String updatedAt;
    private final Integer isDelete;
    private final int commentNum;

    // 기본 생성자
    public ReviewFindResponseDto(Long reviewId, String said, String userId, Integer stars, String content, String createdAt, String updatedAt, Integer isDelete, int commentNum) {
        this.reviewId = reviewId;
        this.said = said;
        this.userId = userId;
        this.stars = stars;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isDelete = isDelete;
        this.commentNum = commentNum;
    }
}
