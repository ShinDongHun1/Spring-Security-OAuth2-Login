package com.loginapi.loginpakage.global.oauth2;

import org.springframework.http.HttpMethod;


public enum SocialType {


    KAKAO(
            "kakao",
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.GET
            ),

    GOOGLE(
            "google",
            "https://www.googleapis.com/oauth2/v3/userinfo",
            HttpMethod.GET
    ),

    NAVER(
            "naver",
            "https://openapi.naver.com/v1/nid/me",
            HttpMethod.GET
    );



    private String socialName;
    private String userInfoUri;
    private HttpMethod method;

    SocialType(String socialName, String userInfoUri, HttpMethod method) {
        this.socialName = socialName;
        this.userInfoUri = userInfoUri;
        this.method = method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getSocialName() {
        return socialName;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }
}
