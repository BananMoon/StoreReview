package com.review.storereview.controller.cust;

import com.review.storereview.common.JwtTokenProvider;
import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.common.exception.ParamValidationException;
import com.review.storereview.common.exception.dto.ExceptionResponseDto;
import com.review.storereview.dao.cust.User;
import com.review.storereview.dto.ResponseJsonObject;
import com.review.storereview.dto.request.TokenDto;
import com.review.storereview.dto.request.UserSigninRequestDto;
import com.review.storereview.dto.response.UserResponseDto;
import com.review.storereview.filter.AuthorizationCheckFilter;
import com.review.storereview.dto.validator.UserSaveDtoValidator;
import com.review.storereview.service.cust.BaseUserService;
import com.review.storereview.dto.request.UserSaveRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;

@RestController
public class UserApiController {
    private final BaseUserService userService;
    private final UserSaveDtoValidator userSaveDtoValidator = new UserSaveDtoValidator();

    @Autowired
    public UserApiController(BaseUserService userService, JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userService = userService;
    }

    /**
     * 회원가입 요청 처리 api
     * @param userSaveRequestDto
     * @return ResponseEntity<ResponseJsonObject>
     */

    @PostMapping("/user/signup")
    public ResponseEntity<ResponseJsonObject> save(@RequestBody UserSaveRequestDto userSaveRequestDto, BindingResult bindingResult) throws NoSuchAlgorithmException {
        System.out.println("UserApiController: save 호출");
        // ResponseJsonOBject 사용
        ResponseJsonObject resDto = null;
        // 파라미터 검증
        userSaveDtoValidator.validate(userSaveRequestDto, bindingResult);

        // 검증 실패 시
        if (bindingResult.hasErrors()) {
            System.out.println(userSaveDtoValidator.getErrorsMap());
            throw new ParamValidationException("회원가입 api 호출 중 파라미터 에러");
//            throw new ParamValidationException(userSaveDtoValidator.getErrorMap());
        }
        else {      // 성공 로직
            User user = userService.join(userSaveRequestDto);
            resDto = new ResponseJsonObject(ApiStatusCode.OK);
            return new ResponseEntity<ResponseJsonObject>(resDto, HttpStatus.OK);
        }
    }


    @PutMapping(value = "/api/sign_in")
    public ResponseEntity<ResponseJsonObject> sign_in(@RequestBody UserSigninRequestDto requestDto)
    {
        ResponseJsonObject resDto = null;
        UserResponseDto responseDto;

        // 1. Validation 필요..


        // 2. Sign_in 서비스 로직
        User user = userService.sign_in(requestDto);

        // 3. response 데이터 가공
        responseDto = UserResponseDto.builder()
                .suid(user.getSuid())
                .said(user.getSaid())
                .birthDate(user.getBirthDate())
                .id(user.getId())
                .gender(user.getGender())
                .name(user.getName())
                .nickname(user.getNickname())
                .phone(user.getPhone())
                .build();

       resDto = new ResponseJsonObject(ApiStatusCode.OK).setData(responseDto);

       // 5.
        return new ResponseEntity<ResponseJsonObject>(resDto, HttpStatus.OK);
    }
    @PutMapping(value = "/api/test")
    public ResponseEntity<ResponseJsonObject> test()
    {
        ResponseJsonObject resDto = null;

        resDto = new ResponseJsonObject(ApiStatusCode.OK).setData("DATA!!");

        // 5.
        return new ResponseEntity<ResponseJsonObject>(resDto, HttpStatus.OK);
    }
}
