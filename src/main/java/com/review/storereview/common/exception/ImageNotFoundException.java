package com.review.storereview.common.exception;

import com.review.storereview.common.enumerate.ApiStatusCode;

// IMAGE 조회 불가 에러
public class ImageNotFoundException extends ReviewServiceException{
    public ImageNotFoundException() {
        super(ApiStatusCode.IMAGE_NOT_FOUND);
    }
}
