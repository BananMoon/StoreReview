package com.review.storereview.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * 로그인 요청 Dto
 */
@Getter
@NoArgsConstructor
public class UserSigninRequestDto {
    @NotEmpty(message = "이메일을 입력해야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "유효한 이메일을 입력해야합니다.")
    private String userId;
    @NotEmpty(message =  "비밀번호를 입력해야 합니다.")
    private String password;

    @Builder
    public UserSigninRequestDto(String userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    public UsernamePasswordAuthenticationToken getNonAuthentication() {
        return new UsernamePasswordAuthenticationToken(userId, password);
    }
}