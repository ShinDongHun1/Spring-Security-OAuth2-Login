package com.loginapi.loginpakage.global.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loginapi.loginpakage.global.oauth2.SocialType;
import com.loginapi.loginpakage.global.oauth2.authentication.AccessTokenSocialTypeToken;
import com.loginapi.loginpakage.global.oauth2.authentication.OAuth2UserDetails;
import com.loginapi.loginpakage.global.oauth2.service.strategy.GoogleLoadStrategy;
import com.loginapi.loginpakage.global.oauth2.service.strategy.KakaoLoadStrategy;
import com.loginapi.loginpakage.global.oauth2.service.strategy.NaverLoadStrategy;
import com.loginapi.loginpakage.global.oauth2.service.strategy.SocialLoadStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class LoadUserService {

    private final RestTemplate restTemplate = new RestTemplate();

    private SocialLoadStrategy socialLoadStrategy;//추상 클래스, 로그인을 진행하는 사이트레 따라 달라짐


    public OAuth2UserDetails getOAuth2UserDetails(AccessTokenSocialTypeToken authentication)  {

        SocialType socialType = authentication.getSocialType();

        setSocialLoadStrategy(socialType);//SocialLoadStrategy 설정

        String socialPk = socialLoadStrategy.getSocialPk(authentication.getAccessToken());//PK 가져오기

        return OAuth2UserDetails.builder() //PK와 SocialType을 통해 회원 생성
                .socialId(socialPk)
                .socialType(socialType)
                .build();
    }

    private void setSocialLoadStrategy(SocialType socialType) {
        this.socialLoadStrategy = switch (socialType){

            case KAKAO -> new KakaoLoadStrategy();
            case GOOGLE ->  new GoogleLoadStrategy();
            case NAVER ->  new NaverLoadStrategy();
            default -> throw new IllegalArgumentException("지원하지 않는 로그인 형식입니다");
        };
    }


}
