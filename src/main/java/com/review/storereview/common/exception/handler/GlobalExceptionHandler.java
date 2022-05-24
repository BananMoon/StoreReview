package com.review.storereview.common.exception.handler;

import com.review.storereview.common.exception.*;
import com.review.storereview.dto.ResponseJsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class       : GlobalExceptionHandler
 * Author      : 문 윤 지
 * Description : controller에서 발생하는 전역적인 예외처리 핸들러
 * History     : [2022-01-08] - Class Create
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    // 사용자 정의 예외
    @ExceptionHandler(PersonAlreadyExistsException.class)  // 회원가입 시 이미 존재하는 회원이 있을 경우 호출
    public ResponseEntity<ResponseJsonObject> handlePersonAlreadyExistsException
            (PersonAlreadyExistsException ex) {
        System.out.println("GlobalExceptionHandler.handlePersonAlreadyExistsException 호출됨");
        return new ResponseEntity<>(ex.getResponseJsonObject(), HttpStatus.CONFLICT);
    }
    @ExceptionHandler(PersonIdNotFoundException.class)  // 존재하지 않는 회원 조회할 경우 호출
    public ResponseEntity<ResponseJsonObject> handlePersonIdNotFoundException
            (PersonIdNotFoundException ex) {
        return new ResponseEntity<>(ex.getResponseJsonObject(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ContentNotFoundException.class)  // 존재하지 않는 콘텐츠 조회할 경우 호출
    public ResponseEntity<ResponseJsonObject> handleReviewNotFoundException
            (ContentNotFoundException ex) {
        return new ResponseEntity<>(ex.getResponseJsonObject(), HttpStatus.NOT_FOUND);
    }

    // 파라미터 유효성 검사 문제 Exception
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        System.out.println("GlobalExceptionHandler.handleMethodArgumentNotValidException 호출됨");
        Map<String, String> parameterErrorMsg = createErrorMsg(ex);
        ParamValidationException pve = new ParamValidationException(parameterErrorMsg);

        return new ResponseEntity<>(pve.getResponseJsonObject(), HttpStatus.BAD_REQUEST);
    }

    private Map<String, String> createErrorMsg(BindException ex) {
        Map<String, String> parameterErrorMsg = new LinkedHashMap<>();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        int idx = 1;
        for (ObjectError Error : allErrors) {
            parameterErrorMsg.put("Error"+ idx++, Error.getDefaultMessage());
        }
        return parameterErrorMsg;
    }
}
