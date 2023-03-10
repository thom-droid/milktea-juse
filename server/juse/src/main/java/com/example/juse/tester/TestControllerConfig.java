package com.example.juse.tester;

import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("local")
@Configuration
public class TestControllerConfig {

    @Autowired
    SocialUserRepository socialUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Bean("init")
    public CommandLineRunner setup() {
        return args -> {

            User user = userRepository.findByEmail("testUser@gmail.com");

            if (user != null) {
                userRepository.delete(user);
            }

            SocialUser socialUser = SocialUser.builder()
                    .name("test")
                    .email("testUser@gmail.com")
                    .build();

            socialUserRepository.findByEmail(socialUser.getEmail()).orElseGet(() -> socialUserRepository.save(socialUser));

            String token = jwtTokenProvider.generateToken(socialUser.getEmail(), "ROLE_USER").getAccessToken();
            System.out.println(token);
        };
    }


}
