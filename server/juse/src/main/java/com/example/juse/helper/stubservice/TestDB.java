package com.example.juse.helper.stubservice;

import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.tag.entity.Tag;
import com.example.juse.tag.repository.TagRepository;
import com.example.juse.tag.repository.UserTagRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("testonly")
@RequiredArgsConstructor
@Component
public class TestDB {

    private final TagRepository tagRepository;
    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;


    @Bean
    public void populateTag() {
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
    }

//    @Bean
    public void populateSocialUser() {

        User data = User.builder()
                .introduction("user1")
                .email("test1@gmail.com")
                .nickname("user1")
                .portfolio("hey")
                .build();

        User user = userRepository.save(data);

        SocialUser socialUser = SocialUser.builder()
                .role("MEMBER")
                .user(user)
                .email("test1@gmail.com")
                .build();

        socialUserRepository.save(socialUser);
    }

}
