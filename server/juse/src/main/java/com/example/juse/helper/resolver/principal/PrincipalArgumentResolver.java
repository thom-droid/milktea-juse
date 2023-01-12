package com.example.juse.helper.resolver.principal;

import com.example.juse.security.oauth.PrincipalDetails;
import com.example.juse.social.entity.SocialUser;
import com.example.juse.user.entity.User;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * this is argument resolver to stub a simple user
 */
public class PrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(PrincipalDetails.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        User user = User.builder()
                .id(1L)
                .nickname("stubbed user1")
                .email("user1 email")
                .introduction("hello")
                .build();

        SocialUser socialUser = SocialUser.builder()
                .user(user)
                .name("user1")
                .role("MEMBER")
                .id(1L)
                .email("user1 email").build();

        PrincipalDetails principalDetails = new PrincipalDetails(socialUser);

        return principalDetails;
    }
}
