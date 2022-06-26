package com.review.storereview.security;

import com.review.storereview.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Class       : SecurityUtil
 * Author      : 문 윤 지
 * Description : Security 관련 필요한 util 클래스
 * History     : [2022-06-06] - 문 윤지 - Class Create
 */
@Component
public class SecurityUtil {
    public CustomUserDetails getUserDetailsFromSecurityContextHolder() {
        //인증된 사용자의 인증객체 가져오기.
        Authentication authenticationToken = SecurityContextHolder.getContext().getAuthentication();
        //인증 객체에 저장되어있는 유저정보 가져오기.
        CustomUserDetails userDetails = (CustomUserDetails) authenticationToken.getPrincipal();
        return userDetails;
    }
}
