package com.example.juse.security.oauth;

import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {
    String provider;
    String providerId;
    String email;
    String img;
    String role;
    String name;

    private final SocialUserRepository socialUserRepository;

    private Map<String, Object> attributes;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("user info : {}", oAuth2User.getAttributes().toString());
        log.info("user Authorities : {}", oAuth2User.getAuthorities());

        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            provider = userRequest.getClientRegistration().getClientId();
            providerId = oAuth2User.getAttribute("sub");
            email = oAuth2User.getAttribute("email");
            img = oAuth2User.getAttribute("picture");
            role = "ROLE_USER";
            name = oAuth2User.getAttribute("name");
        }

        if(userRequest.getClientRegistration().getRegistrationId().equals("github")) {
            provider =  userRequest.getClientRegistration().getClientId();
            providerId = oAuth2User.getAttribute("id").toString();
            name = oAuth2User.getAttribute("login");
            email = name + "@github.com";
            img = oAuth2User.getAttribute("avatar_url");
            role = "ROLE_USER";
        }

        log.info("email check 2 : {}", email);

        SocialUser socialuser = socialUserRepository.findByEmail(email)
                .orElseGet(() -> {
                            SocialUser newSocialUser = SocialUser.builder()
                                    .email(email)
                                    .role(role)
                                    .provider(provider)
                                    .providerId(providerId)
                                    .name(name)
                                    .img(img)
                                    .build();

                            return socialUserRepository.save(newSocialUser);
                        }
                );
        
        log.info(socialuser.toString());
        attributes = oAuth2User.getAttributes();

        return new PrincipalDetails(socialuser, oAuth2User.getAttributes());
    }

    public PrincipalDetails loadUserByEmail(String email) {

        SocialUser socialUser = socialUserRepository.findByEmail(email).orElseThrow(NoSuchElementException::new);
        return new PrincipalDetails(socialUser, attributes);
    }
}
