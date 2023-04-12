package com.example.juse.config;

import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.tag.entity.Tag;
import com.example.juse.tag.repository.TagRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@TestConfiguration
public class JpaTestDBInstance {

    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;
    private final TagRepository tagRepository;

    public JpaTestDBInstance(UserRepository userRepository, SocialUserRepository socialUserRepository, TagRepository tagRepository) {
        this.userRepository = userRepository;
        this.socialUserRepository = socialUserRepository;
        this.tagRepository = tagRepository;
    }

    @Bean
    public void populateTestUser() {
        Tag tag = Tag.builder()
                .type(Tag.Type.BACKEND)
                .name("java")
                .build();

        Tag tag2 = Tag.builder()
                .type(Tag.Type.BACKEND)
                .name("python")
                .build();

        Tag tag3 = Tag.builder()
                .type(Tag.Type.FRONTEND)
                .name("react")
                .build();

        tagRepository.saveAll(List.of(tag, tag2, tag3));

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
                .nickname("user1")
                .portfolio("hey")
                .build();

        User data2 = User.builder()
                .socialUser(socialUser2)
                .introduction("user2")
                .email(socialUser2.getEmail())
                .nickname("user2")
                .portfolio("noob")
                .build();

        User user = userRepository.save(data);
        userRepository.save(data2);
    }
}
