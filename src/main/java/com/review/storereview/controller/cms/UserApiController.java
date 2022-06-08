package com.review.storereview.controller.cms;

import com.review.storereview.common.enumerate.ApiStatusCode;
//import com.review.storereview.common.jwt.JwtAuthenticationFilter;
import com.review.storereview.common.jwt.JwtTokenProvider;
import com.review.storereview.dto.ResponseJsonObject;
import com.review.storereview.dto.response.TokenResponseDto;
import com.review.storereview.dto.request.UserSigninRequestDto;
import com.review.storereview.common.jwt.AuthorizationCheckFilter;
import com.review.storereview.repository.cms.BaseUserRepository;
import com.review.storereview.service.cms.BaseUserService;
import com.review.storereview.dto.request.UserSaveRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
/**
 * Class       : UserApiController
 * Author      : 문 윤 지
 * Description : 회원가입 요청 api 컨트롤러
 * History     : [2022-01-02] - Class Create
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserApiController {
    private final BaseUserRepository userRepository;
    private final BaseUserService userService;
//    private final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 요청 처리 api
     * @param userSaveRequestDto
     * @return ResponseEntity<ResponseJsonObject>
     */
    @PostMapping("/signup")
    public ResponseEntity<ResponseJsonObject> save(@Valid @RequestBody UserSaveRequestDto userSaveRequestDto) throws NoSuchAlgorithmException {
        // 1. join 서비스 로직
        userService.join(userSaveRequestDto);
        // 2. responseDto 생성
        ResponseJsonObject resDto = ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode());
        return new ResponseEntity<>(resDto, HttpStatus.OK);
    }

    /**
     * 로그인 요청 처리 api
     * @param loginDto
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody UserSigninRequestDto loginDto){
        // 1. 유저 존재여부 체크 (Repository 접근) -> loadUserByUsername()에서 UsernameNotFoundException을 던져서 생략해도 될듯

        // 2. id, pwd 기반으로 인증처리되지 않은 Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.getNonAuthentication();
        Authentication authentication = null ;
        String jwt = "";
        // 3. 실제 검증 진행
        try {
            authentication =  authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 보안 주체의 세부 정보를 포함하여 응용프래그램의 현재 보안 컨텍스트에 대한 세부 정보가 저장
            // 현재 SecurityContext(Authentication을 보관하는 역할)를 리턴하여 그 Authentication 세팅
            // 세션이 아닌 토큰을 사용하기 때문에 아래 line은 안해도 될듯.
//            SecurityContextHolder.getContext().setAuthentication(authentication);
        // 4. 인증 정보 기반으로 JWT 토큰 생성
            jwt = jwtTokenProvider.generateToken(authentication);
        }catch(AuthenticationException ex)  // 인증 절차 실패시 리턴되는 Exception
        {
            log.debug("AuthController Auth 체크 실패 "+ ex.getMessage());
            ex.printStackTrace();
            return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.UNAUTHORIZED.getCode()), HttpStatus.UNAUTHORIZED);
        }catch(Exception ex)
        {
            log.error("AuthController Exception : " + ex.getMessage());
            return new ResponseEntity<>(ResponseJsonObject.withError(ApiStatusCode.SYSTEM_ERROR.getCode(), ApiStatusCode.SYSTEM_ERROR.getType(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }   // 체크 필요!
        // 5. Http Header에 "Authorization"를 Key로 갖도록 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AuthorizationCheckFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
//        httpHeaders.add(AuthorizationCheckFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        // body, header, status를 인자로 받는 생성자로 반환
        return new ResponseEntity<>(ResponseJsonObject.withStatusCode(ApiStatusCode.OK.getCode()).setData(new TokenResponseDto(jwt)), httpHeaders, HttpStatus.OK);
    }
}