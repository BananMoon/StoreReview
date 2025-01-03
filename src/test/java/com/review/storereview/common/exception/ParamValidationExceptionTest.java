package com.review.storereview.common.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;

import java.util.HashMap;

class ParamValidationExceptionTest {

    ObjectMapper om ;

    @BeforeEach
    void beforeEach() {
        om = new ObjectMapper().registerModule(new JavaTimeModule());

    }

    @Test
    @DisplayName("추가 메시지를 지닌 예외의 경우")
    void 파라미터예외_테스트 ()
    {
        ParamValidationException ex =  Assertions.assertThrows(ParamValidationException.class, ()
        ->{
            throw new ParamValidationException(new HashMap<String, String>() {{
                put("testKey", "testValue");
            }});
        });

        try {
            System.out.println(om.writeValueAsString(ex.getResponseJsonObject()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("추가 메시지 없는 예외의 경우")
    void 파라미터예외_테스트2 ()
    {
        ParamValidationException ex =  Assertions.assertThrows(ParamValidationException.class, ()
                ->{
            throw new ParamValidationException();
        });

        try {
            System.out.println(om.writeValueAsString(ex.getResponseJsonObject()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}