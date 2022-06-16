package com.review.storereview.service.cms;

import com.review.storereview.dto.request.ReviewUploadRequestDto;
import com.review.storereview.dto.response.ReviewFindResponseDto;
import com.review.storereview.dto.response.ReviewResponseDto;
import com.review.storereview.repository.cms.ImageRepository;
import com.review.storereview.repository.cms.BaseCommentRepository;
import com.review.storereview.repository.cms.BaseReviewRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * {@Summary Service Layer 단위 테스트}
 * Class       : ReviewServiceImplTest
 * Author      : 문 윤 지
 * History     : [2022-01-23]
 */
//@ExtendWith(MockitoExtension.class)
//@SpringBootTest
class ReviewServiceImplTest {
//    @Mock BaseReviewRepository reviewRepository;
//    @InjectMocks ReviewServiceImpl reviewService;
    @Autowired ReviewServiceImpl reviewService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final BaseReviewRepository reviewMockRepository = mock(BaseReviewRepository.class);
    private final ImageRepository imageRepository = mock(ImageRepository.class);
    private final BaseCommentRepository commentRepository = mock(BaseCommentRepository.class);
    private final ReviewServiceImpl mockService = new ReviewServiceImpl(commentRepository, reviewMockRepository, imageRepository);

//    @Test
    void 리뷰_업로드() {
        // when
//        List<String> imgUrl = new ArrayList<>(Arrays.asList("http://s3-img-url-test3.com","http://s3-img-url-test4.com", "http://s3-img-url-test5.com"));
        Integer stars = 4;
        List<Long> imgIds = new ArrayList<>(Arrays.asList(1L, 2L));

        ReviewUploadRequestDto uploadRequestDto = new ReviewUploadRequestDto("1234", "리뷰 서비스 테스트3", stars, imgIds);
        ReviewResponseDto testUploadedReview = mockService.uploadReview(null, uploadRequestDto);

        // verify
        Assertions.assertThat(testUploadedReview.getContent()).isEqualTo("리뷰 서비스 테스트3");
        // 출력
        System.out.println(testUploadedReview);
    }

//    @Test
    void 리뷰_조회() {
        //when
        Long reviewId = 4L;
        ReviewFindResponseDto findOneReview =  reviewService.getReview(reviewId);
        // verify
        Assertions.assertThat(findOneReview.getContent()).isEqualTo("리뷰 서비스 테스트");
        // 출력
        System.out.println(findOneReview);
    }

    //    @Test
    void 가게_리뷰_전체_조회() {
        // when
        /*System.out.println("리뷰 전체 조회");
        String placeId = "1234";
        List<Review> findReviews = reviewService.getAllReviews(placeId);
        System.out.println("조회 완료");
        System.out.println(findReviews.size());
        for (Review review : findReviews) {
            // verify
            System.out.println(review.toString());
            Assertions.assertThat(review.getPlaceId()).isEqualTo(placeId);
        }*/
    }

//    @Test
    void 리뷰_수정() {
        /*List<Long> imageIds = new ArrayList<>(Arrays.asList(1,2));
        // Collections.emptyList();
        Integer stars=1;
        ReviewUpdateRequestDto updateRequestDto = new ReviewUpdateRequestDto("업데이트된 리뷰 서비스 테스트", imageIds, stars);
        Review review = new Review().builder()
                .content(updateRequestDto.getContent())
                .stars(updateRequestDto.getStars())
                .imageIds(null)
                .build();
        // when : 조회
        Long reviewId = 4L;
        Review findOneReview =  reviewService.getReview(reviewId);
        // verify
        Assertions.assertThat(findOneReview.getContent()).isEqualTo("리뷰 서비스 테스트");

        // when
        Review updatedReview = reviewService.updateReview(findOneReview, review);

        // verify
        Assertions.assertThat(updatedReview.getContent()).isEqualTo(review.getContent());*/

    }
    @Rollback
    @Transactional
//    @Test
    void 리뷰_삭제() {
        // when
        /*Long reviewId = 5L;
        reviewService.deleteReview(reviewId);

        // 해당 리뷰가 없다면 ReviewNotFoundException을 던진다.
        ContentNotFoundException exception = assertThrows(ContentNotFoundException.class,
                () -> reviewService.getReview(reviewId));

        // then
        assertEquals("컨텐츠 없음.", exception.getResponseJsonObject().getMeta().getErrorMsg());*/
    }

//    @Test
    @DisplayName("리뷰 0개인 가게 리뷰 조회 시")
    void 가게_없는_리뷰_전체_조회() {
        // when
       /* System.out.println("리뷰 전체 조회");
        String placeId = "12345678";
        ReviewFindListResponseDto findReviews = reviewService.getAllReviews(placeId);
        System.out.println("조회 완료");
        System.out.println(findReviews.size());
        if (findReviews.size()==0) {
            System.out.println(findReviews);
        }
        else {
            for (ReviewFindListResponseDto review : findReviews) {
                // verify
                System.out.println(review.toString());
                Assertions.assertThat(review.getPlaceId()).isEqualTo(placeId);
            }
        }*/

    }
}