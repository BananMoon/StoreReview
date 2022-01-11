package com.review.storereview.common.exception;

import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.common.exception.dto.ExceptionResponseDto;

// 회원가입 시 이미 회원이 존재하는 경우 발생하는 에러

public class PersonAlreadyExistsException extends RuntimeException{
    private final ApiStatusCode errorStatusCode = ApiStatusCode.PERSON_ALREADY_EXISTS;
    private final ExceptionResponseDto exceptionResponseDto;

    public ExceptionResponseDto getExceptionResponseDto(){
        return exceptionResponseDto;
    }

    public PersonAlreadyExistsException() {
        exceptionResponseDto = ExceptionResponseDto.createMetaDto(errorStatusCode);
    }
}
