package com.review.storereview.common;

import com.review.storereview.service.cms.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Class       : ImageClearScheduler
 *  Author      : 문 윤 지
 *  Description : 1주에 1번 3주전에 업데이트 된 업로드되지않은 이미지 정리 스케줄러
 *  History     : [2022-06-27] Create
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ImageClearScheduler {
    private final ImageService imageService;
    // 매주 월요일 새벽에 조회하여 이미지 테이블의 필드와 S3 저장소의 데이터 삭제
    // 조건 1. 업데이트 날짜: 3주 (이상) 전 날짜
    // 조건 2. reviewId가 null
    @Scheduled(cron = "0 0 3 * * 0")
    public void clearDeletedImage() {
        DateTime now = DateTime.now();
        // TODO: 2022-06-27 로그를 남겨둬야할 것 같다.
        log.info("ImageClearScheduler.clearDeletedImage() 실행 (" + now + ")");

        imageService.clearUnusedImagesInDBAndS3for3weeks(now.toDate());
    }
}