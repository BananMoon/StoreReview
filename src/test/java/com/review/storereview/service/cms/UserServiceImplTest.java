package com.review.storereview.service.cms;

import com.review.storereview.common.enumerate.Gender;
import com.review.storereview.common.exception.PersonAlreadyExistsException;
import com.review.storereview.dto.request.UserSaveRequestDto;
import com.review.storereview.repository.cms.BaseUserRepository;
import com.review.storereview.service.cms.UserServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl 테스트")
class UserServiceImplTest {

    @Mock private BaseUserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks private UserServiceImpl userService;

    LocalDate birthDate = LocalDate.of(1999, 11, 15);

    @Test
    public void 유저_생성() {
        // given
        UserSaveRequestDto userSaveRequestDto = new UserSaveRequestDto("moonz99@naver.com", "문", "moonz", "1234567", birthDate, Gender.W, "01012345678");    // hibernate: 같은 KEY값은 UPDATE

        // when
        userService.join(userSaveRequestDto);
    }

    // 실행 X : Expected com.review.storereview.common.exception.PersonAlreadyExistsException to be thrown, but nothing was thrown.
    @Test
    @DisplayName("이미 있는 ID로 회원가입시 실패")
    public void 중복회원생성_예외() throws RuntimeException {
        // given
        LocalDate birthDate = LocalDate.now();
        UserSaveRequestDto userSaveRequestDto = UserSaveRequestDto.builder().userId("banan99@naver.com").name("뭉지").nickname("moon").password("123456").birthDate(birthDate).gender(Gender.W).phone("01013572468")
                .build();
        // when
        userService.join(userSaveRequestDto);
        PersonAlreadyExistsException ex = assertThrows(PersonAlreadyExistsException.class,
                () -> userService.join(userSaveRequestDto));
//        assertThat(ex.getMessage()).isEqualTo("이미 존재하는 사용자입니다.");
    }
}