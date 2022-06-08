package com.review.storereview.dto.request;

import com.review.storereview.dao.cms.User;
import com.review.storereview.common.enumerate.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

/**
 * 회원가입 요청 Dto
 */
@Getter
@NoArgsConstructor
public class UserSaveRequestDto{
    @NotEmpty(message = "이메일을 입력해야 합니다.")
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$", message = "유효한 이메일을 입력해야합니다.")
    private String userId;
    @NotEmpty(message =  "비밀번호를 입력해야 합니다.")
    private String password;
    @NotEmpty(message = "이름을 입력해야합니다.")
    private String name;
    private String nickname;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    @NotNull(message = "성별을 입력해야합니다.")
    private Gender gender;
    @Pattern(regexp = "^\\d{3}\\d{3,4}\\d{4}$", message = "핸드폰 번호를 알맞게 작성해야 합니다. ex) 01012345678")
    private String phone;
    @Builder
    public UserSaveRequestDto(String userId, String password, String name, String nickname, LocalDate birthDate, Gender gender, String phone) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.nickname = nickname;
        this.birthDate = birthDate;
        this.gender = gender;
        this.phone = phone;
    }

    // Dto에서 필요한 부분을 entity화
    public User toEntity() {
        return User.builder()
                .userId(userId)
                .password(password)
                .name(name)
                .nickname(nickname)
                .birthDate(birthDate)
                .gender(gender)
                .phone(phone)
                .build();
    }
}
