package com.review.storereview.config;

import com.review.storereview.security.jwt.JwtTokenProvider;
import com.review.storereview.common.enumerate.Authority;
import com.review.storereview.common.exception.handler.AuthenticationExceptionHandler;
import com.review.storereview.common.exception.handler.AuthorizationExceptionHandler;
import com.review.storereview.security.jwt.AuthorizationCheckFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Class       : SecurityConfig
 * Author      : 조 준 희
 * Description : Security 설정 객체
 * History     : [2022-01-10] - 조 준희 - Class Create
 */
@Configuration
@EnableWebSecurity  // Spring Security 사용
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 3. provider
    private final JwtTokenProvider jwtTokenProvider;
    // 4. 401,403 Handler
    private final AuthenticationExceptionHandler authenticationExceptionHandler;
    private final AuthorizationExceptionHandler authorizationExceptionHandler;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    public SecurityConfig( JwtTokenProvider jwtTokenProvider
    , AuthenticationExceptionHandler authenticationExceptionHandler
    , AuthorizationExceptionHandler authorizationExceptionHandler
    , AuthenticationManagerBuilder authenticationManagerBuilder
    , UserDetailsService userDetailsService) throws Exception {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationExceptionHandler = authenticationExceptionHandler;
        this.authorizationExceptionHandler = authorizationExceptionHandler;
        this.authenticationManagerBuilder = authenticationManagerBuilder;

        // Provider 추가 설정  기본적으로 DaoAuthenticationProvider가 있음
        // Default Provider를 설정함. => Default 실패시 DaoAuthenticationProvider의 authenticate가 실행.
        authenticationManagerBuilder.authenticationProvider(jwtTokenProvider);

    }

    /**
     * 전반적인 Spring Security의 설정
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // csrf보안 disable
                .csrf().disable()
                .formLogin() .disable()
                //  예외 처리 지정
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationExceptionHandler)       //401 Error Handler
                    .accessDeniedHandler(authorizationExceptionHandler)                //403 Error Handler

                // enable h2-console
                    .and()
                .headers()
                .frameOptions()
                .sameOrigin()       // 동일 도메인에서는 iframe 접근 가능

                // jwt사용, 세션 사용X => STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .cors().configurationSource(corsConfigurationSource())
                /**
                 * URI별 인가 정보 셋팅.
                 */
                .and()
                .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // Preflight Request 요청 모두 허용
                    .antMatchers(HttpMethod.GET,"/comment/**").permitAll()
                    .antMatchers(HttpMethod.POST,"/comment").hasRole(Authority.USER.getName())
                    .antMatchers(HttpMethod.PUT,"/comment").hasRole(Authority.USER.getName())
                    .antMatchers(HttpMethod.DELETE,"/comment").hasRole(Authority.USER.getName())
                    .antMatchers(HttpMethod.GET, "/reviews/**","/places/**" ).permitAll()
                    .antMatchers("/login", "/signup", "/test/ping").permitAll()    // 인증 절차 없이 접근 허용(로그인 관련 url)
//                    .antMatchers("/authenticate", "/signup", "/test/ping").permitAll()    // 인증 절차 없이 접근 허용(로그인 관련 url)
                    .antMatchers("/review", "/test/tester").hasRole(Authority.USER.getName())
                    .antMatchers("/test/admin").hasRole(Authority.ADMIN.getName())
                    .anyRequest().authenticated()       // 그 외 나머지 리소스들은 무조건 인증을 완료해야 접근 가능
                .and()
                //AuthenticationFilterChain- UsernamePasswordAuthenticationFilter 전에 실행될 커스텀 필터 등록
                .addFilterBefore(new AuthorizationCheckFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
                //.apply(new JwtSecurityConfig(jwtTokenProvider));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // - (3)
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedOrigin("https://store-review.nextwing.me");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
