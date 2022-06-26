package com.review.storereview.controller.cms;

import com.review.storereview.security.jwt.JwtTokenProvider;
import com.review.storereview.common.enumerate.ApiStatusCode;
import com.review.storereview.dto.ResponseJsonObject;
import com.review.storereview.dto.response.TokenResponseDto;
import com.review.storereview.dto.request.UserSigninRequestDto;
import com.review.storereview.security.jwt.AuthorizationCheckFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Class       : AuthController
 * Author      : 조 준 희
 * Description : 사용자 인증/인가 컨트롤러
 * History     : [2022-01-10] - 조 준희 - Class Create
 */
//@RestController
public class AuthController {
    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final JwtTokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

   @Autowired
    public AuthController( JwtTokenProvider jwtTokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.tokenProvider = jwtTokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    /**
     *
     * @param loginDto
     * @return header, body, status code를 모두 포함한 ResponseEntity
     */
    @PostMapping("/authenticate")
    public ResponseEntity<Object> authorize(@RequestBody UserSigninRequestDto loginDto){
        // 1. Principal, Credential 정보로 Authentication Token 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUserId(), loginDto.getPassword());
        Authentication authentication = null ;
        String jwt = "";

        try {
            authentication =  authenticationManagerBuilder.getObject().authenticate(authenticationToken);   // AbstractSecurityBuilder의 object로 authenticate()호출

            SecurityContextHolder.getContext().setAuthentication(authentication);

            jwt = tokenProvider.generateToken(authentication);
        }catch(AuthenticationException ex)  // 인증 절차 실패시 리턴되는 Exception
        {
            logger.debug("AuthController Auth 체크 실패 "+ ex.getMessage());
            ex.printStackTrace();
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.UNAUTHORIZED.getCode()), HttpStatus.UNAUTHORIZED);
        }catch(Exception ex)
        {
            logger.error("AuthController Exception : " + ex.getMessage());
            return new ResponseEntity<>(ResponseJsonObject.withError(ApiStatusCode.SYSTEM_ERROR.getCode(), ApiStatusCode.SYSTEM_ERROR.getType(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }   // 체크 필요!

        // header에 jwt 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AuthorizationCheckFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()).setData(new TokenResponseDto(jwt)), httpHeaders, HttpStatus.OK);
    }

}
