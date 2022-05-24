package com.review.storereview.common.exception;

import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.dto.ResponseJsonObject;
// handler에서 잡으려면 RuntimeException을 상속받아야하나?
public class ReviewServiceException extends RuntimeException{
    protected ApiStatusCode errorStatusCode ;
    protected ResponseJsonObject responseJsonObject;

    public ResponseJsonObject getResponseJsonObject(){
        return responseJsonObject;
    }

    public ReviewServiceException() {
    }

    public ReviewServiceException(ApiStatusCode errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
        responseJsonObject = ResponseJsonObject.withError(errorStatusCode.getCode(), errorStatusCode.getType(), errorStatusCode.getMessage());
    }

}
