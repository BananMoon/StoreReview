package com.review.storereview.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
/**
 * {@Summary Image Upload 응답 객체 }
 * Class       : ImageUploadResponseDto
 * Author      : 문 윤 지
 * History     : [2022-06-13]
 */
@Getter
@AllArgsConstructor
public class ImageUploadResponseDto {
    // 이미지ID, 리뷰ID, URL 주소, CREATE AT
    private Long imageId;
    private Long reviewId;
    private String fileUrl;
}
