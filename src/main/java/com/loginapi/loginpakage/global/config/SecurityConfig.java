package com.loginapi.loginpakage.global.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //== 접근 제한 ==//
        http.authorizeRequests()
                .antMatchers("/login/oauth2/*").permitAll() //로그인 화면 접근 가능
                .antMatchers("/").permitAll() //메인 화면 접근 가능
                .anyRequest().hasRole("USER");




    }
}
