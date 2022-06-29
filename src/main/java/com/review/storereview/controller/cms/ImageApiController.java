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

import java.util.List;

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

    // TODO: 2022-06-26 가게 조회 화면에서는 이미지 썸네일을 보여주고, 리뷰를 클릭했을 때 이미지를 보여주도록?
    // TODO: 2022-06-26 이미지 조회는 어떤 값으로 조회할지? reviewId로 먼저 조회하고 그 reviewId로 이미지를 조회해야할 것 같은데
    @GetMapping("/review/{reviewId}/images")
    public ResponseEntity<ResponseJsonObject> findImages(@PathVariable Long reviewId) {
        ResponseJsonObject resJsonObj = ResponseJsonObject
                .withStatusCode(ApiStatusCode.OK.getCode())
                .setData(imageService.listAll(reviewId));

        return new ResponseEntity<>(resJsonObj, HttpStatus.OK);
    }

    @PostMapping("/review/image")
    public ResponseEntity<ResponseJsonObject> createImage(@RequestPart(value = "imgFile", required = false) MultipartFile imgFile) {
        ImageUploadResponseDto image = imageService.uploadImage(imgFile);
        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()).setData(image);
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
