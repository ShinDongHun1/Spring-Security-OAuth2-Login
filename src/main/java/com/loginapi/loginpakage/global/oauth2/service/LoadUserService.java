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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoadUserService {

    private final RestTemplate restTemplate = new RestTemplate();

    private SocialLoadStrategy socialLoadStrategy;


    public OAuth2UserDetails getOAuth2UserDetails(AccessTokenSocialTypeToken authentication)  {
        SocialType socialType = authentication.getSocialType();

        setSocialLoadStrategy(socialType);

        String socialPk = socialLoadStrategy.getSocialPk(authentication.getAccessToken());

        return OAuth2UserDetails.builder()
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
