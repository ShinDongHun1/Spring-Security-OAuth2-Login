package com.loginapi.loginpakage.global.oauth2.service.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

public abstract class SocialLoadStrategy {

    ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE  =  new ParameterizedTypeReference<>(){};

    protected final RestTemplate restTemplate = new RestTemplate();

    public String getSocialPk(String accessToken) {
        HttpHeaders headers = new HttpHeaders();

        setHeaders(accessToken, headers);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();


        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        return sendRequestToSocialSite(request);
    }

    protected abstract String sendRequestToSocialSite(HttpEntity request);



    public void setHeaders(String accessToken, HttpHeaders headers) {
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }
}
