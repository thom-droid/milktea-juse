package com.example.juse.sse;

import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
public class TestDB {
    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TestDB(SocialUserRepository socialUserRepository, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.socialUserRepository = socialUserRepository;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public void populate() {
        userRepository.findByNickname("testUser1").ifPresent(userRepository::delete);
        userRepository.findByNickname("testUser2").ifPresent(userRepository::delete);
        socialUserRepository.findByEmail("test1@gmail.com").ifPresent(socialUserRepository::delete);
        socialUserRepository.findByEmail("test2@gmail.com").ifPresent(socialUserRepository::delete);

        SocialUser socialUser = SocialUser.builder()
                .role("MEMBER")
                .email("test1@gmail.com")
                .build();

        SocialUser socialUser2 = SocialUser.builder().role("MEMBER").email("test2@gmail.com").build();
        SocialUser socialUser1 = socialUserRepository.save(socialUser);
        socialUserRepository.save(socialUser2);

        User data = User.builder()
                .socialUser(socialUser1)
                .introduction("user1")
                .email("test1@gmail.com")
                .nickname("testUser1")
                .portfolio("hey")
                .build();

        User data2 = User.builder()
                .socialUser(socialUser2)
                .introduction("user2")
                .email(socialUser2.getEmail())
                .nickname("testUser2")
                .portfolio("noob")
                .build();

        User user = userRepository.save(data);
        User user2 = userRepository.save(data2);

        String testToken = jwtTokenProvider.generateToken(user.getEmail(), "MEMBER_ROLE").getAccessToken();
        String testToken2 = jwtTokenProvider.generateToken(user2.getEmail(), "MEMBER_ROLE").getAccessToken();

        System.out.println("test user1: " + user.getEmail());
        System.out.println("test user2: " + user2.getEmail());

        System.out.println("test user token :" + testToken);
        System.out.println("test user2 token : " + testToken2);
    }
}
