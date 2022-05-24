package com.review.storereview.controller.cms;

import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.common.exception.PersonAlreadyExistsException;
import com.review.storereview.common.exception.PersonIdNotFoundException;
import com.review.storereview.dto.ResponseJsonObject;
import com.review.storereview.service.cms.BaseUserService;
import com.review.storereview.dto.request.UserSaveRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
/**
 * Class       : UserApiController
 * Author      : 문 윤 지
 * Description : 회원가입 요청 api 컨트롤러
 * History     : [2022-01-02] - Class Create
 */
@RestController
public class UserApiController {
    private final BaseUserService userService;
    @Autowired
    public UserApiController(BaseUserService userService) {
        this.userService = userService;
    }

    /**
     * 회원가입 요청 처리 api
     * @param userSaveRequestDto
     * @return ResponseEntity<ResponseJsonObject>
     */
    @PostMapping("/api/signup")
    public ResponseEntity<ResponseJsonObject> save(@Valid @RequestBody UserSaveRequestDto userSaveRequestDto) throws NoSuchAlgorithmException {
        // 1. join 서비스 로직
        userService.join(userSaveRequestDto);
/*
        try {

        } catch(PersonAlreadyExistsException ex) {
            System.out.println("UserApiController.save에서 예외 catch!");
            return new ResponseEntity<>(ex.getResponseJsonObject(), HttpStatus.BAD_REQUEST);
        }
*/
        System.out.println("예외 처리 발생 후 userApiController line");
        // 2. responseDto 생성
        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }
}