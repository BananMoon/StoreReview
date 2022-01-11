package com.review.storereview.config;

import com.review.storereview.common.JwtTokenProvider;
import com.review.storereview.common.exception.handler.AuthenticationExceptionHandler;
import com.review.storereview.common.exception.handler.AuthorizationExceptionHandler;
import com.review.storereview.filter.AuthorizationCheckFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Class       : SecurityConfig
 * Author      : 조 준 희
 * Description : Security 설정 객체
 * History     : [2022-01-10] - 조 준희 - Class Create
 */
@EnableWebSecurity
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

        // Provider 설정
        authenticationManagerBuilder.authenticationProvider(jwtTokenProvider);

        // DaoAuthenticationProvider가 사용하는 Service로 설정
        try {
            authenticationManagerBuilder.userDetailsService(userDetailsService);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 전반적인 Spring Security의 설정
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf().disable()
                .formLogin() .disable()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationExceptionHandler)       //401 Error Handler
                    .accessDeniedHandler(authorizationExceptionHandler)                //403 Error Handler

                // enable h2-console
                    .and()
                .headers()
                .frameOptions()
                .sameOrigin()

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                /**
                 * URI별 인가 정보 셋팅.
                 */
                .and()
                .authorizeRequests()
                    .antMatchers("/authenticate").permitAll()
                    .antMatchers("/user/signup").permitAll()
                    .antMatchers("/api/sign_in").permitAll()
                    .anyRequest().authenticated()
                .and()
                //AuthenticationFilterChain- UsernamePasswordAuthenticationFilter 전에 실행될 필터 세팅.
                .addFilterBefore(new AuthorizationCheckFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
                //.apply(new JwtSecurityConfig(jwtTokenProvider));
    }
}
