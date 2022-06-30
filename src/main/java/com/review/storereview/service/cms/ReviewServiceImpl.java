package com.review.storereview.service.cms;

import com.review.storereview.common.exception.ContentNotFoundException;
import com.review.storereview.common.exception.CustomAuthenticationException;
import com.review.storereview.common.exception.ImageNotFoundException;
import com.review.storereview.common.utils.CryptUtils;
import com.review.storereview.common.utils.StringUtil;
import com.review.storereview.dao.cms.Comment;
import com.review.storereview.security.CustomUserDetails;
import com.review.storereview.dao.cms.Image;
import com.review.storereview.dao.cms.Review;
import com.review.storereview.dao.cms.User;
import com.review.storereview.dto.request.ReviewUpdateRequestDto;
import com.review.storereview.dto.request.ReviewUploadRequestDto;
import com.review.storereview.dto.response.ReviewFindListResponseDto;
import com.review.storereview.dto.response.ReviewFindResponseDto;
import com.review.storereview.dto.response.ReviewResponseDto;
import com.review.storereview.repository.cms.ImageRepository;
import com.review.storereview.repository.cms.BaseCommentRepository;
import com.review.storereview.repository.cms.BaseReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * {@Summary Review Service Layer }
 * Class       : ReviewServiceImpl
 * Author      : 문 윤 지
 * History     : [2022-01-23]
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl {
    private final BaseCommentRepository commentRepository;
    private final BaseReviewRepository baseReviewRepository;
    private final ImageRepository imageRepository;
    private final CryptUtils cryptUtils;

    /** {@Summary place에 해당하는 n개의 리뷰 데이터 리스트 조회 Service (2차원 리스트)} */
    public ReviewFindListResponseDto getAllReviews(String placeId) {
        List<Review> reviews = Optional.ofNullable(baseReviewRepository.findAllByPlaceIdAndIsDeleteIsOrderByCreatedAtDesc(placeId, 0))
                .orElseThrow(ContentNotFoundException::new);
        Double placeAvgStars = calculateAverageOfPlaceStars(reviews);

        // listResponseDto 생성 및 list에 추가
        ReviewFindListResponseDto listResponseDto = ReviewFindListResponseDto.createReviewsResponseDto(placeAvgStars);
        for (Review review : reviews) {
            listResponseDto.addReview(
                    new ReviewFindResponseDto(
                            review.getReviewId(),
                            (review.getUser().getSaid()),
                            review.getUser().getUserId(),
                            review.getStars(),
                            CryptUtils.Base64Encoding(review.getContent()),
                            StringUtil.DateTimeToString(review.getCreatedAt()),
                            StringUtil.DateTimeToString(review.getUpdatedAt()),
                            review.getIsDelete(),
                            commentRepository.findCommentNumByReviewId(review.getReviewId())
                    )
            );
        }
        return listResponseDto;
    }

    /** {@Summary 특정 리뷰 데이터 조회 Service}*/
    public ReviewFindResponseDto getReview(Long reviewId) {
        // 1. 리뷰 데이터 조회 & null 체크
        Review review = Optional.ofNullable(baseReviewRepository.findByReviewIdAndIsDeleteIs(reviewId, 0))
                .orElseThrow(ContentNotFoundException::new);

        return new ReviewFindResponseDto(
                review.getReviewId(),
                (review.getUser().getSaid()),
                review.getUser().getUserId(),
                review.getStars(),
                CryptUtils.Base64Encoding(review.getContent()),
                StringUtil.DateTimeToString(review.getCreatedAt()),
                StringUtil.DateTimeToString(review.getUpdatedAt()),
                review.getIsDelete(),
                commentRepository.findCommentNumByReviewId(review.getReviewId())
        );
    }

    /**{@Summary 리뷰 업로드 Service} */
    @Transactional
    public ReviewResponseDto uploadReview(CustomUserDetails userDetails, ReviewUploadRequestDto requestDto) {
        // 1. 리뷰 생성
        Review review = Review.createReview(requestDto);
        review.setUser(User.builder()
                .userId(userDetails.getUsername())  // Name == userId(이메일)
                .suid(userDetails.getSuid())
                .said(userDetails.getSaid())
                .build());

        //  2. 이미지 테이블의 reviewID setting
        if (!isEmptyDoubleCheck(review.getImageIds())) {
            for (Long imgId : review.getImageIds()) {
                Image image = imageRepository.findByImageId(imgId);
//            Image image = Optional.ofNullable(imageRepository.findByImageId(imgId))
//                    .orElseThrow(ImageNotFoundException::new);
                image.setReviewId(review.getReviewId());
            }
        }
        baseReviewRepository.save(review);
        // 3. dto 생성
        return new ReviewResponseDto(
                review.getReviewId(),
                cryptUtils.callAESEncode(review.getUser().getSaid()),
                userDetails.getUsername(),
                review.getStars(), review.getContent(),
                review.getImageIds(),
                StringUtil.DateTimeToString(review.getCreatedAt()),
                StringUtil.DateTimeToString(review.getUpdatedAt())
                );
    }

    /** {@Summary 리뷰 업데이트 Service} */
    @Transactional
    public ReviewResponseDto updateReview(String suid, Long reviewId, ReviewUpdateRequestDto requestDto) throws CustomAuthenticationException {
        // 기존 리뷰 조회
        Review existReview = baseReviewRepository.findByReviewIdAndIsDeleteIs(reviewId, 0);
        if (!writerAuthorityCheck(existReview.getUser().getSuid(),suid)){
            throw new CustomAuthenticationException();
//            return new ResponseEntity<>(ResponseJsonObject.withError(ApiStatusCode.FORBIDDEN.getCode(), ApiStatusCode.FORBIDDEN.getType(), ApiStatusCode.FORBIDDEN.getMessage()), HttpStatus.FORBIDDEN);
        }

        // img 테이블 set
        Image.insertReviewId(imageRepository.findAllByReviewId(reviewId), reviewId);

        existReview.updateReview(
                CryptUtils.Base64Decoding(requestDto.getContent()),
                requestDto.getStars(),
                requestDto.getImgIds()
        );

        // responseDto 반환
        return new ReviewResponseDto(
                existReview.getReviewId(),
                cryptUtils.callAESEncode(existReview.getUser().getSaid()),
                existReview.getUser().getUserId(),
                existReview.getStars(), existReview.getContent(),
                existReview.getImageIds(),
                StringUtil.DateTimeToString(existReview.getCreatedAt()),
                StringUtil.DateTimeToString(existReview.getUpdatedAt())
        );
    }

    /**{@Summary 리뷰 데이터 제거 Service} **/
    @Transactional
    public void deleteReview(String suid, Long reviewId) throws CustomAuthenticationException {
        // 기존 리뷰 조회
        Review existReview = Optional.ofNullable(baseReviewRepository.findByReviewIdAndIsDeleteIs(reviewId, 0))
                .orElseThrow(ImageNotFoundException::new);
        if (!writerAuthorityCheck(existReview.getUser().getSuid(),suid)){
            throw new CustomAuthenticationException();
//            return new ResponseEntity<>(ResponseJsonObject.withError(ApiStatusCode.FORBIDDEN.getCode(), ApiStatusCode.FORBIDDEN.getType(), ApiStatusCode.FORBIDDEN.getMessage()), HttpStatus.FORBIDDEN);
        }
        // imageIds 있는지 체크
        if (!isEmptyDoubleCheck(existReview.getImageIds())) {
            Image.deleteAll(imageRepository.findAllByReviewId(reviewId));
        }
        existReview.deleteReview();
        // 코멘트의 댓글들도 isDelete 세팅
        List<Comment> relatedComments = commentRepository.findAllByReviewIdAndIsDelete(existReview.getReviewId(), 0);
        if (!isEmptyDoubleCheck(relatedComments)) {
            for (Comment comment: relatedComments) {
                comment.setIsDelete(1);
            }
        }
    }
    /**
     * 프론트로부터 혹은 DB로부터 전달된 객체가 비었는지 체크하기 위해
     * null인 경우와 빈 배열일 경우를 동시에 체크한다.
     */
    private boolean isEmptyDoubleCheck(List<?> objects) {
        if (!Objects.isNull(objects))
            if (!objects.isEmpty()) {
                return false;
            }
        return true;
    }
    /** 작성자의 suid와 사용자의 suid를 비교하여 검증한다. */
    private boolean writerAuthorityCheck(String writerSuid, String userSuid) {
        System.out.println("ReviewServiceImpl.writerAuthorityCheck에서 걸러지는건가?");
        if (writerSuid.equals(userSuid))
            return true;
        return false;
    }

    /**
     * {@Summary 특정 가게의 리뷰글들의 평균 계산하여 가게 평균 구하는 Service}
     * @param findReviews
     */
    private Double calculateAverageOfPlaceStars(List<Review> findReviews) {
        Double sum = .0;
        for(Review review : findReviews) {
            sum += review.getStars();
        }
        return sum / findReviews.size();
    }
}