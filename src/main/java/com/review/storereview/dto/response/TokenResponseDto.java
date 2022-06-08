package com.review.storereview.dto.response;

import lombok.*;


/**
 * Class       : TokenDto
 * Author      : 조 준 희
 * Description : JWT토큰 발행 ResponseDTO
 * History     : [2022-01-10] - 조 준희 - Class Create
 *               [2022-06-01] - 문 윤지 - Class Refactor : 위치 이동, 클래스명 TokenResponseDto로 변경
 */
@Getter
public class TokenResponseDto {

    private String token;

    public TokenResponseDto(String token) {
        this.token = token;
    }
}