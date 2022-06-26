package com.review.storereview.service.cms;

import com.review.storereview.security.CustomUserDetails;
import com.review.storereview.dao.cms.User;
import com.review.storereview.repository.cms.BaseUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Class       : AuthService
 * Author      : 조 준 희
 * Description : 사용자 인증/인가 서비스
 * History     : [2022-01-10] - 조 준희 - Class Create
 */
@RequiredArgsConstructor
@Component
public class AuthService implements UserDetailsService {
    private final BaseUserRepository userRepository;

    /**
     * AuthenticationProvider가 호출하는 loadUserByUsername 오버라이딩 함수.
     * @param username
     * @return
     * @throws AuthenticationException
     */
    @Override
    public UserDetails loadUserByUsername(final String username) throws AuthenticationException {

        User result = userRepository.findOneByUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " -> 일치하는 사용자가 없습니다..") );

        return createUser(result);
    }
    /**
     * DTO.User객체를 UserDetails객체로 변환.
     * @param user
     * @return
     */
    private CustomUserDetails createUser(User user) {
//        if (!user.isActivated()) {
//            throw new RuntimeException(username + " -> 활성화되어 있지 않습니다.");
//        }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        grantedAuthorities.add(new SimpleGrantedAuthority(user.getRole().getFullName()));

        return new CustomUserDetails(user.getUserId(),
                user.getPassword(),
                grantedAuthorities,
                user.getSuid(),
                user.getSaid());
    }
}
