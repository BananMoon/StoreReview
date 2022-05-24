package com.review.storereview.common.exception;

import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.dto.ResponseJsonObject;

import java.util.Map;

/**
 * Class       : ParamValidationException
 * Author      : 문 윤 지
 * Description : api 요청 시 전달하는 파라미터에 문제 발생 시 던져지는 Exception
 * History     : [2022-01-07] - Class Create
 */
public class ParamValidationException extends ReviewServiceException{
    // for test
    public ParamValidationException() {
        super(ApiStatusCode.PARAMETER_CHECK_FAILED);
    }

    public ParamValidationException(Map<String, String> parameterErrorMsg) {
        super();
        System.out.println("ParamValidationException.ParamValidationException 호출됨");
        super.errorStatusCode = ApiStatusCode.PARAMETER_CHECK_FAILED;
        super.responseJsonObject = ResponseJsonObject.withParameterMsg(
                errorStatusCode.getCode(), errorStatusCode.getType(), errorStatusCode.getMessage(), parameterErrorMsg
        );
    }
}
