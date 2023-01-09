package com.example.juse.helper.stubservice;

import com.example.juse.board.entity.Board;
import com.example.juse.board.repository.BoardRepository;
import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.tag.entity.Tag;
import com.example.juse.tag.repository.TagRepository;
import com.example.juse.tag.repository.UserTagRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDate;
import java.util.List;

@Profile("testonly")
@RequiredArgsConstructor
@Configuration
public class TestDB {

    private final TagRepository tagRepository;
    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final BoardRepository boardRepository;


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

    @Bean
    public void populateSocialUser() {

        SocialUser socialUser = SocialUser.builder()
                .role("MEMBER")
                .email("test1@gmail.com")
                .build();

        SocialUser socialUser1 = socialUserRepository.save(socialUser);

        User data = User.builder()
                .socialUser(socialUser1)
                .introduction("user1")
                .email("test1@gmail.com")
                .nickname("user1")
                .portfolio("hey")
                .build();

        User user = userRepository.save(data);

        Board board = Board.builder()
                .type(Board.Type.PROJECT)
                .user(user)
                .title("test1")
                .content("test1")
                .url("http://localhost:8080/board/1")
                .people(5)
                .contact("contact")
                .dueDate(LocalDate.now())
                .startingDate(LocalDate.now())
                .period("6")
                .onOffline("online")
                .build();

        boardRepository.save(board);

    }

}
