package com.review.storereview.common.exception;

import com.review.storereview.common.enumerate.ApiStatusCode;
// 콘텐츠 (Review, Comment를 포괄) 조회 불가 에러
public class ContentNotFoundException extends ReviewServiceException{
    public ContentNotFoundException() {
        super(ApiStatusCode.CONTENT_NOT_FOUND);
    }
}
