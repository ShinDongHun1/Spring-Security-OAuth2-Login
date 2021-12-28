# AccessToken만 가지고 OAuth2 로그인하기

<br/><br/><br/><br/>

## 진행과정

UsernamePasswordAuthenticationFilter 필터 바로 이전에
AbstractAuthenticationProcessingFilter를 상속받은 필터를 등록하여, AccessToken을 이용한 로그인 처리를 시작할 것임.

해당 Filter가 갖는 AuthenticationManager는 AccessToken을 가지고 인증을 수행하는
Provider를 단 하나만 가지고 있음.


해당 Provider는 당연히 AuthenticationProvider를 상속받고, AccessToken과 URI의 끝에 붙은 정보 (ex : kakao, google)를
이용하여 RestTemplate를 사용해 회원 정보를 받아옴.

받아온 회원 정보에 대해, DB를 조회하여, 해당 회원이 이미 등록되어 있으면 그대로 반환, 그렇지 않으면 DB에 저장 후 반환.

Authentication 객체에 저장되는 객체는 UserDetails를 상속받은 MemberDetails로 할것임
(굳이 UserDetails를 상속받는 이유는, 이후 소셜 로그인이 아닌, 자체 로그인을 진행할 때 사용하는 UserDatial과 통일하기 위함)
