package com.review.storereview.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

/**
 * 테스트중(only 로컬)
 */
@Setter
@Getter
public class ReviewUploadWithFileRequestDto {
    private String placeId;
    private String content;
    private Integer stars;

    @Nullable
    private FileInfoDto fileInfoDto;

    public ReviewUploadWithFileRequestDto(String placeId, String content, Integer stars) {
        this.placeId = placeId;
        this.content = content;
        this.stars = stars;
    }
}
