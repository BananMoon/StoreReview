package com.review.storereview.controller.cms;

import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.common.exception.ContentNotFoundException;
import com.review.storereview.common.exception.ParamValidationException;
import com.review.storereview.security.SecurityUtil;
import com.review.storereview.common.utils.CryptUtils;
import com.review.storereview.security.CustomUserDetails;
import com.review.storereview.dto.ResponseJsonObject;
import com.review.storereview.dto.request.ReviewUpdateRequestDto;
import com.review.storereview.dto.request.ReviewUploadRequestDto;
import com.review.storereview.dto.response.ReviewFindResponseDto;
import com.review.storereview.dto.response.ReviewFindListResponseDto;
import com.review.storereview.dto.response.ReviewResponseDto;
import com.review.storereview.service.cms.ReviewServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * {@Summary 리뷰 api 요청 컨트롤러 }
 * Author      : 문 윤 지
 * History     : [2022-01-23]
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class ReviewApiController {
    private final ReviewServiceImpl reviewService;
    private final SecurityUtil securityUtil;

    /**
     * {@Summary 특정 가게에 대한 전체 리뷰 조회 컨트롤러}
     * @param placeId
     */
    @GetMapping("/places/{placeId}")
    public ResponseEntity<ResponseJsonObject> findAllReviews(@PathVariable String placeId) throws ContentNotFoundException{
        // 1. findAll 서비스 로직
        ReviewFindListResponseDto responseDtos = reviewService.getAllReviews(placeId); // 해당하는 장소 관련 리뷰들 모두 조회하여 리스트
        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()).setData(responseDtos);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * {@Summary 한개의 리뷰 조회 컨트롤러 }
     * @param reviewId
     */
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseJsonObject> findOneReview(@PathVariable Long reviewId) throws ContentNotFoundException {
        // 1. 조회 서비스 로직 (리뷰 조회 - userId 조회)
        ReviewFindResponseDto reviewResponseDto = reviewService.getReview(reviewId);

        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()).setData(reviewResponseDto);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * {@Summary 리뷰 작성 컨트롤러}
     * @param requestDto
     */
    @PostMapping("/review")
    public ResponseEntity<ResponseJsonObject> uploadReview(@RequestParam("key") ReviewUploadRequestDto requestDto) {
        // 파라미터 검증
        Map<String, String> errorsMap = checkParameterValid(requestDto);
        if (errorsMap.size() >= 1) {
            ResponseJsonObject exceptionDto = new ParamValidationException(errorsMap).getResponseJsonObject();
            return new ResponseEntity<>(exceptionDto, HttpStatus.BAD_REQUEST);
        }
        // 1. 인증된 사용자 토큰 값 : 인증된 사용자의 인증 객체로 유저 정보 가져오기
        CustomUserDetails userDetails = securityUtil.getUserDetailsFromSecurityContextHolder();

        // 2. 인코딩된 content 디코딩
        String decodedContent = CryptUtils.Base64Decoding(requestDto.getContent());
        requestDto.setContent(decodedContent);

        // 5. 리뷰 업로드 서비스 호출
        ReviewResponseDto reviewResponseDto = reviewService.uploadReview(userDetails, requestDto);
        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.CREATED.getCode()).setData(reviewResponseDto);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 바인딩된 ReviewUploadRequestDto를 검증한다.
     * @param requestDto
     */
    private Map<String, String> checkParameterValid(ReviewUploadRequestDto requestDto) {
        Map<String, String> errorsMap = new HashMap<>();
        String defaultMessage;

        // 1. stars 체크
        if (requestDto.getStars() < -1 || requestDto.getStars() > 6) {
            defaultMessage = "stars는 1 이상 5 이하의 정수로 작성해야 합니다.";
            errorsMap.put("stars", defaultMessage);
        }
        // 2. content null 체크
        if (requestDto.getContent() == null) {
            defaultMessage = "내용을 입력해야합니다.";
            errorsMap.put("content", defaultMessage);
        }
        return errorsMap;
    }

    /**
     * {@Summary 리뷰 업데이트 컨트롤러}
     * @param reviewId
     * @param requestDto
     */
//    @PutMapping("/reviews/{reviewId}")
    @RequestMapping(value="/reviews/{reviewId}", method = RequestMethod.PUT)
    public ResponseEntity<ResponseJsonObject> updateReview(@PathVariable Long reviewId,
                                                           @RequestParam(value = "key") ReviewUpdateRequestDto requestDto) throws ContentNotFoundException{
        // 인증된 사용자 토큰 값 : 인증된 사용자의 인증 객체로 유저 정보 가져오기
        CustomUserDetails userDetails = securityUtil.getUserDetailsFromSecurityContextHolder();
        ReviewResponseDto reviewResponseDto = reviewService.updateReview(userDetails.getSuid(), reviewId, requestDto);

        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()).setData(reviewResponseDto);
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * {@Summary 리뷰 제거 컨트롤러}
     * @param reviewId
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<ResponseJsonObject> deleteReview(@PathVariable Long reviewId) throws ContentNotFoundException{
        // 인증된 사용자 토큰 값 :
        CustomUserDetails userDetails = securityUtil.getUserDetailsFromSecurityContextHolder();
        reviewService.deleteReview(userDetails.getSuid(), reviewId);

        // responseDto 생성
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()), HttpStatus.OK);
    }
}