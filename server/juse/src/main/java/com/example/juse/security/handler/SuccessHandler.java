package com.example.juse.security.handler;

import com.example.juse.helper.utils.UriUtils;
import com.example.juse.security.config.OAuthProperties;
import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.security.jwt.TokenDto;
import com.example.juse.security.oauth.PrincipalDetails;
import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class SuccessHandler extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {


    private final JwtTokenProvider jwtTokenProvider;
    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;
    private final OAuthProperties oAuthProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        // 받아온 객체를 Map 형식으로 저장.
        Map<String, Object> attributes = principalDetails.getAttributes();

        // 토큰을 발급받기 위해 SocialUser 객체로 이름과 email을 받아온다.
        SocialUser socialUser = SocialUser.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .build();

        // Github 로그인일 경우
        SocialUser githubUser = findByEmailGithub((String) attributes.get("email"));

        if (request.getRequestURI().equals("/login/oauth2/code/github") && githubUser.getEmail() == null) {
            socialUser.setEmail(attributes.get("login") + "@github.com");
        }

        // email을 기준으로 토큰 발급
        TokenDto tokenDto = jwtTokenProvider.generateToken(socialUser.getEmail(), "ROLE_USER");

        response.setHeader("Auth", tokenDto.getAccessToken());
        response.setHeader("Refresh", tokenDto.getRefreshToken());

        String redirectUrl = oAuthProperties.getRedirectUrl();
        String token = tokenDto.getAccessToken();
        boolean isSocialUserNull = socialUser.getEmail() == null;
        String redirectUri = UriUtils.getRedirectUriAfterOAuthAuthorized(redirectUrl, token, isSocialUserNull);

        // 최초 로그인 시 추가 회원가입하기 위해 이동.
        response.sendRedirect(redirectUri);
    }

    public SocialUser findByEmailGithub(String email) {
        return socialUserRepository.findGitByEmail(email).orElseGet(SocialUser::new);
    }
}
