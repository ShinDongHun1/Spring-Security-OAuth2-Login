package com.loginapi.loginpakage.global.oauth2.provider;

import com.loginapi.loginpakage.domain.Member;
import com.loginapi.loginpakage.domain.Role;
import com.loginapi.loginpakage.domain.repository.MemberRepository;
import com.loginapi.loginpakage.global.oauth2.authentication.AccessTokenSocialTypeToken;
import com.loginapi.loginpakage.global.oauth2.authentication.OAuth2UserDetails;
import com.loginapi.loginpakage.global.oauth2.service.LoadUserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Component
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final LoadUserService loadUserService;
    private final MemberRepository memberRepository;


    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        OAuth2UserDetails oAuth2User = loadUserService.getOAuth2UserDetails((AccessTokenSocialTypeToken) authentication);
        Member member = saveOrGet(oAuth2User);
        oAuth2User.setRoles(member.getRole().name());

        return AccessTokenSocialTypeToken.builder().principal(oAuth2User).authorities(oAuth2User.getAuthorities()).build();
    }



    private Member saveOrGet(OAuth2UserDetails oAuth2User) {
        return memberRepository.findBySocialTypeAndSocialId(oAuth2User.getSocialType(),
                        oAuth2User.getSocialId())
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .socialType(oAuth2User.getSocialType())
                        .socialId(oAuth2User.getSocialId())
                        .role(Role.GUEST).build()));
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenSocialTypeToken.class.isAssignableFrom(authentication);
    }


    public List<GrantedAuthority> roles(String... roles) {
        List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
        for (String role : roles) {
            Assert.isTrue(!role.startsWith("ROLE_"),
                    () -> role + " cannot start with ROLE_ (it is automatically added)");
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return new ArrayList<>(authorities);
    }
}
