package com.review.storereview.security.jwt;

import com.review.storereview.common.utils.CryptUtils;
import com.review.storereview.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class       : JwtTokenProvider
 * Author      : 조 준 희
 * Description : JWT 토큰 공급자 객체.
 * History     : [2022-01-10] - 조 준희 - Class Create
 */
@Slf4j
@Component
public class JwtTokenProvider implements AuthenticationProvider {

//    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";   // 사용자 권한 체크 위함

    private final String secret;
    private final long tokenExpiryInMilliseconds;
    private Key key;

    private final UserDetailsService userDetailsService;
    private PasswordEncoder passwordEncoder;
    private final CryptUtils cryptUtils;

    @Autowired
    public JwtTokenProvider(@Value("${jwt.secret}")String secret,
                            @Value("${jwt.token-validity-in-seconds}")long tokenExpiryInSeconds,
                            UserDetailsService userDetailsService,
                            PasswordEncoder passwordEncoder,
                            CryptUtils cryptUtils) {
        this.secret = secret;
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.secret));
        this.tokenExpiryInMilliseconds = tokenExpiryInSeconds * 1000;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.cryptUtils = cryptUtils;
    }

    /**
     * Request 받은 Authentication(Login)이 인증유저인지 확인하는 인증절차.
     * @param authentication
     * @return 인증 토큰 생성
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();
        try {
            // Service에서 패스워드비교까지 모두 처리.
            // null일경우 UsernameNotFoundException Throw
            CustomUserDetails userDetails = (CustomUserDetails)(userDetailsService.loadUserByUsername(username));

            passwordChecks(userDetails, (UsernamePasswordAuthenticationToken) authentication);

            return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
        }
        catch(UsernameNotFoundException ex)
        {
            throw ex;
        }
        catch(BadCredentialsException ex)
        {
            throw ex;
        }
    }
    /**
     * 인증 절차 - 비밀번호 체크
     * @param userDetails 데이터 베이스에서 검색한 유저의 정보
     * @param authentication 인증 요청한 요청 정보
     * @throws AuthenticationException
     */
    private void passwordChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException
    {
        if (authentication.getCredentials() == null) {
            log.debug("Failed to authenticate since no credentials provided");
//            throw new BadCredentialsException(this.messages
//                    .getMessage("JwtTokenProvider.badCredentials", "Bad credentials"));
            throw new BadCredentialsException("Bad credentials");
        }
        String presentedPassword = authentication.getCredentials().toString();
        if (!this.passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
            log.debug("Failed to authenticate since password does not match stored value");
//            throw new BadCredentialsException(this.messages
//                    .getMessage("JwtTokenProvider.badCredentials", "Bad credentials"));
            throw new BadCredentialsException("Bad credentials");
        }

    }

    /**
     *  authentication 타입이 UsernamePasswordAuthentiacationToken인 경우만 지원하는 필터 명시
     * @param authentication
     * @return
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }


    /** Authentication 정보로 AccessToken 생성
     * @param authentication
     * @return AccessToken
     */
    public String generateToken (Authentication authentication) throws Exception
    {
        // 권한 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.tokenExpiryInMilliseconds);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt ="";
        // jwt 토큰 생성
        try {
            jwt = Jwts.builder()
                    .setSubject(authentication.getName())
                    .claim(AUTHORITIES_KEY, authorities)
                    //.claim("suid",  userDetails.getSuid())
                    //.claim("said", userDetails.getSaid())
                    .claim("suid", cryptUtils.AES_Encode(((CustomUserDetails) authentication.getPrincipal()).getSuid()))
                    .claim("said", cryptUtils.AES_Encode(((CustomUserDetails) authentication.getPrincipal()).getSaid()))
                    .signWith(key, SignatureAlgorithm.HS512)
                    .setExpiration(validity)
                    .compact();
        }catch(Exception e) {
            log.debug("JWT Token 생성 Exception "+ e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return jwt;
    }


    /**
     * 유효한 토큰으로 Authentication 객체 생성
     * @param token
     * @return Authentication 객체
     * JWT의 페이로드에 들어가는 정보들을 Claim이라 부른다.
     * iss : 데이터의 발행자
     * iat : 데이터가 발행된 시간
     * exp : 데이터가 만료된 시간
     * sub : 토큰의 제목
     * aud : 토큰의 대상
     * nbf : 토큰이 처리되지 않아야 할 시점   * 이 시점이 지나기 전엔 토큰이 처리되지 않습니다.
     * jti : 토큰의 고유 식별자
     * suid
     * said
     */
    public Authentication getAuthentication(String token) throws BadCredentialsException {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Claim에서 권한 정보(헤더 auth) 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
        String suid = null, said = null ;
        try {

            suid = cryptUtils.AES_Decode(claims.get("suid").toString());
            said = cryptUtils.AES_Decode(claims.get("said").toString());

        } catch (Exception e) {
            log.error("Token get Authentication Error "+ e.getMessage());
            throw new BadCredentialsException("");
        }
        // UserDetails 객체 만들어서 Authentication 객체 리턴
        CustomUserDetails principal = new CustomUserDetails(claims.getSubject(), "", authorities, suid, said);
//        new JWTUserDetails(claims.getSubject(), "", authorities, suid, said);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);

    }

    /**
     * Token 유효성 검사. jwt를 파싱해서 나오는 Claim에 대해 예외처리.
     * @param token
     * @return true if token is validate
     *         false if token is not validate. (wrong jwt signkey, expired jwt token, unsupported jwt, illegal jwt token)
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

}
