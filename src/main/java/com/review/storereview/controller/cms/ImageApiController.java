package com.review.storereview.controller.cms;

import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.dto.ResponseJsonObject;
import com.review.storereview.dto.response.ImageUploadResponseDto;
import com.review.storereview.service.cms.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Class       : ImageApiController
 * Author      : 문 윤 지
 * Description : 이미지 관련 http 요청 컨트롤러
 * History     : [2022-06-09] - 문 윤지 - Class Create
*/
@RequiredArgsConstructor
@RestController
public class ImageApiController {
    private final ImageService imageService;

    @PostMapping("/review/image")
    public ResponseEntity<ResponseJsonObject> createImage(@RequestPart(value = "imgFile", required = false) MultipartFile imgFile) {
        ImageUploadResponseDto image = imageService.uploadImage(imgFile);
        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()).setData(image.getImageId());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    @DeleteMapping("/review/image/{imageId}")
    public void deleteImage(@PathVariable Long imageId) {
        imageService.deleteImage(imageId);
    }

    // 리뷰 글을 제거하는 경우에만 호출된다.
    @DeleteMapping("/review/{reviewId}/images")
    public void deleteAllImages (@PathVariable Long reviewId) {
        imageService.deleteAllImages(reviewId);
    }
}
