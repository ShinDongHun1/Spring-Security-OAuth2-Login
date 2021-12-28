# AccessToken만 가지고 OAuth2 로그인하기

<br/><br/><br/><br/>

## 코드의 동작 순서
<br/><br/>
로그인 요청이 들어온다 -> AbstractAuthenticationProcessingFilter이 작동한다



-> AbstractAuthenticationProcessingFilter가 attemptAuthentication()를 호출하는데, 이는 추상 메서드로 AbstractAuthenticationProcessingFilter를 구현한 하위 클래스에서 구현해야 한다.



-> 우리는 이를 구현한 OAuth2AccessTokenAuthenticationFilter를 등록할 것이고, 따라서 OAuth2AccessTokenAuthenticationFilter의 attemptAuthentication()이 작동한다. 해당 메서드에서는 실행중에 AutenticationManager의 authenticate()를 호출하여 인증을 진행한다.



->AutenticationManager의 authenticate()가 동작한다. 이때 인자로는 Authentication을 커스텀하게 구현한 AccessTokenSocialTypeToken를 전달한다.



->AutenticationManager에는 우리가 커스텀한 Provider인 AccessTokenAuthenticationProvider가 들어있으며, AutenticationManager는 authenticate()메서드 실행 중에 AccessTokenAuthenticationProvider의 authenticate()를 호출한다.



->AccessTokenAuthenticationProvider 의 authenticate() 메소드 속에서 우리는 AccessToken을 가지고 회원의 정보를 받아와야 한다.  이 역할을 LoadUserService의 getOAuth2UserDetails()가 실행한다.



-> getOAuth2UserDetails()에서는 SocialLoadStrategy 를 사용하여 socialType마다 다르게 RestTemplate을 통해 정보를 조회하는 요청을 보낸다. SocialLoadStrategy 의 getSocialPk() 를 통해 해당 소셜 로그인 API의 식별값을 받아온다.



-> LoadUserService에서는 받아온 식별자와 socialType을 통해 OAuth2UserDetails을 생성하여 반환한다.(OAuth2UserDetails는 UserDetails 구현체로 후에 ContextHolder에서 꺼내어서 사용할 때, UserDetail형식으로 통일하여 사용하기 위해 이렇게 구현하였다)



->AccessTokenAuthenticationProvider에서는 받아온 OAuth2UserDetails에서 식별자와 socialType으로 DB에서 회원을 조회한다. 만약 조회되는 회원이 없다면 새로 등록해준다. 이후 Authentcitaion 구현체인 AccessTokenSocialTypeToken 객체를 만들어서 반환한다.



->AccessTokenSocialTypeToken 는 principal로 OAuth2UserDetails를 가진다. 해당 AccessTokenSocialTypeToken 는 AccessTokenAuthenticationProvider에서 반환되어 AutenticationManager로 반환되고, 최종적으로 AbstractAuthenticationProcessingFilter에게 반환된다.



-> 문제 없이 로그인이 잘 되었다면 successfulAuthentication()가 실행된다. 해당 메소드에서는 인증된 Authentication 객체를 SecurityContextHolder에 저장하고, AuthenticationSuccessHandler를 호출하여 로그인 성공 처리를 한다.

(실패시에는 당연히 FailureHandler를 실행하겠지?)





(너무 복잡해서 조금이라도 알아보기 편하라고 색깔을 추가했다)
