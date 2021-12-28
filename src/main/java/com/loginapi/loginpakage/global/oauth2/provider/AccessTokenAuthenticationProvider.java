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
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {//AuthenticationProvider을 구현받아 authenticate와 supports를 구현해야 한다.

    private final LoadUserService loadUserService;  //restTemplate를 통해서 AccessToken을 가지고 회원의 정보를 가져오는 역할을 한다.
    private final MemberRepository memberRepository;//받아온 정보를 통해 DB에서 회원을 조회하는 역할을 한다.



    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {//ProviderManager가 호출한다. 인증을 처리한다

        OAuth2UserDetails oAuth2User = loadUserService.getOAuth2UserDetails((AccessTokenSocialTypeToken) authentication);
        //OAuth2UserDetails는  UserDetails를 상속받아 구현한 클래스이다. 이후 일반 회원가입 시 UserDetails를 사용하는 부분과의 다형성을 위해 이렇게 구현하였다.
        //getOAuth2UserDetails에서는 restTemplate과 AccessToken을 가지고 회원정보를 조회해온다 (식별자 값을 가져옴)


        Member member = saveOrGet(oAuth2User);//받아온 식별자 값과 social로그인 방식을 통해 회원을 DB에서 조회 후 없다면 새로 등록해주고, 있다면 그대로 반환한다.
        oAuth2User.setRoles(member.getRole().name());//우리의 Role의 name은 ADMIN, USER, GUEST로 ROLE_을 붙여주는 과정이 필요하다. setRolse가 담당한다.

        return AccessTokenSocialTypeToken.builder().principal(oAuth2User).authorities(oAuth2User.getAuthorities()).build();
        //AccessTokenSocialTypeToken객체를 반환한다. principal은 OAuth2UserDetails객체이다. (formLogin에서는 UserDetails를 가져와서 결국 ContextHolder에 저장하기 때문에)
        //이렇게 구현하면 UserDetails 타입으로 회원의 정보를 어디서든 조회할 수 있다.
    }



    private Member saveOrGet(OAuth2UserDetails oAuth2User) {
        return memberRepository.findBySocialTypeAndSocialId(oAuth2User.getSocialType(),
                                                            oAuth2User.getSocialId())  //socailID(식별값)과 어떤 소셜 로그인 유형인지를 통해 DB에서 조회
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .socialType(oAuth2User.getSocialType())
                        .socialId(oAuth2User.getSocialId())
                        .role(Role.GUEST).build()));//없다면 멤버를 새로 만드는데, USER가 아니라 GUEST로 설정했다. 이는 아래해서 설명한다
    }



    @Override
    public boolean supports(Class<?> authentication) {
        return AccessTokenSocialTypeToken.class.isAssignableFrom(authentication); //AccessTokenSocialTypeToken타입의  authentication 객체이면 해당 Provider가 처리한다.
    }


    /**
     * 왜 GUEST로 설정하였나??
     *
     * 소셜 로그인에서 필수로 제공하는 회원의 정보들이 다 다르다.
     * 예를 들면 카카오는 이름을 email을 필수로 제공하지 않는데, 구글은 email이 필수일 뿐더러 식별값으로 사용한다.
     * 이렇게 소셜 로그인간의 정보의 불균형을 해소하기 위해 우리는 소셜 로그인 시에 아무런 정보도 받아오지 않고,
     * 단지 식별값만을 사용하여 회원을 저장한다.
     * 현재 GUEST로 저장된 상황은 클라이언트가 정보 제공에 동의를 누른 경우에 해당 코드가 실행된다.
     *
     * 정보 동의를 한 이후 우리는 추가 정보를 입력받으러 추가 폼 입력 화면으로 리다이렉트 시킨다.
     * 그럼 클라이언트는 추가 정보를 입력하고 AccessToken과 함께 다시 추가 정보를 서버에 전달하면 된다.
     *
     * 그런데 우리는 해당 회원이 이미 가입을 한 회원인지, 아니면 정보 제공에 동의만 하고 가입은 진행하기 전 회원인지 알 수 있는 방법이 딱히 없다.
     * 그래서 그냥 물론 다른 방법도 있겠지만 간단하게 ROLE을 GUEST로 주어서 아직 회원가입을 진행하지 않고 DB에 저장된 상태라는 것을 표시하는 것이다.
     *
     * (근데 지금 생각해보니까, 인가 코드를 이미 프론트엔드에서 받아오기 때문에 굳이 이럴 필요가 없는 거 같긴 하다.. 엥..ㅠㅠ 이후 제대로 실험해본 후 수정하겠다.)
     *
     *
     */


}
