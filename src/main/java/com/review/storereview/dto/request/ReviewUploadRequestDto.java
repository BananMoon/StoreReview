package com.review.storereview.dto.request;

import lombok.*;
import java.util.List;

/**
 * {@Summary 리뷰 작성 요청 클래스 (DTO) }
 * INPUT : [placeId, imgUrl, content, stars]
 * Author      : 문 윤 지
 * History     : [2022-01-23]
 */
@Getter
@NoArgsConstructor
public class ReviewUploadRequestDto {
    private String placeId;
    @Setter
    private String content;
    private Integer stars;
    private List<Long> imgIds;

    @Builder
    public ReviewUploadRequestDto(String placeId, String content, Integer stars, List<Long> imgIds) {
        this.placeId = placeId;
        this.content = content;
        this.stars = stars;
        this.imgIds = imgIds;
    }

    // Dto에서 필요한 부분을 entity화
/*    public Review toEntity() {
        return Review.builder()
                .placeId(placeId)
                .content(content)
                .stars(stars)
                .imgUrl(imgUrl)
                .build();
    }*/
}
