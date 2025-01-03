package com.review.storereview.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Class       : AuthenticationCheckFilter
 * Author      : 조 준 희
 * Description : 요청의 header 내에 jwt 토큰이 Bearer 토큰으로 들어있는지 체크 => 권한(인가) 체크
 * History     : [2022-01-10] - 조 준희 - Class Create
 */
public class AuthorizationCheckFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationCheckFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final JwtTokenProvider tokenProvider ;

    public AuthorizationCheckFilter(JwtTokenProvider provider) {
        this.tokenProvider = provider;
    }

//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
//        String jwt = resolveToken(httpServletRequest);
//        String requestURI = httpServletRequest.getRequestURI();
//
//        // token이 유효한지 확인
//        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
//            Authentication authentication = tokenProvider.getAuthentication(jwt);
//            SecurityContextHolder.getContext().setAuthentication(authentication);       // token에 authentication 정보 삽입
//            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
//        } else {
//            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
//        }
//
//        chain.doFilter(request, response);
//    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws BadCredentialsException,ServletException, IOException {
        String token = resolveToken(request);
        String requestURI = request.getRequestURI();

        // token이 유효한지 확인
        if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token); // 해당 토큰에 해당하는 Authentication 객체 얻기(UserDetails 통해서)
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(request, response);
    }
    // 요청 Header에서 Token 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);                   // Authorization 헤더 꺼냄
        //  요청된 header("Authorization")의 value가 있으면 String 반환
        //  없으면 null 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {     // JWT 토큰이 존재하는지 확인(null 체크 & Bearer로 시작하는가?)
            return bearerToken.substring(7);           // "Bearer"를 제거한 accessToken 반환
        }
        return null;
    }
}
