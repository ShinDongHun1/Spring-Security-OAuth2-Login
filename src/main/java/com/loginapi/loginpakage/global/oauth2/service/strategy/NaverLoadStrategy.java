package com.loginapi.loginpakage.global.oauth2.service.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loginapi.loginpakage.global.oauth2.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class NaverLoadStrategy extends SocialLoadStrategy{


    protected String sendRequestToSocialSite(HttpEntity request){
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(SocialType.NAVER.getUserInfoUrl(),//
                    SocialType.NAVER.getMethod(),
                    request,
                    RESPONSE_TYPE);


            Map<String , Object> response2 = ( Map<String , Object>)response.getBody().get("response");
            return response2.get("id").toString();


        } catch (Exception e) {
            log.error("AccessToken을 사용하여 NAVER 유저정보를 받아오던 중 예외가 발생했습니다 {}" ,e.getMessage() );
            throw e;
        }
    }
}
