package com.example.juse;

import com.example.juse.board.entity.Board;
import com.example.juse.board.repository.BoardRepository;
import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.tag.entity.BoardTag;
import com.example.juse.tag.entity.Tag;
import com.example.juse.tag.repository.TagRepository;
import com.example.juse.tag.repository.UserTagRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@TestConfiguration
public class TestDBInstance {

    public TestDBInstance(TagRepository tagRepository,
                          SocialUserRepository socialUserRepository,
                          UserRepository userRepository,
                          UserTagRepository userTagRepository,
                          BoardRepository boardRepository,
                          JwtTokenProvider jwtTokenProvider) {
        this.tagRepository = tagRepository;
        this.socialUserRepository = socialUserRepository;
        this.userRepository = userRepository;
        this.userTagRepository = userTagRepository;
        this.boardRepository = boardRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    private final TagRepository tagRepository;
    private final SocialUserRepository socialUserRepository;
    private final UserRepository userRepository;
    private final UserTagRepository userTagRepository;
    private final BoardRepository boardRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public void populate() {

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

        Board board = Board.builder()
                .type(Board.Type.PROJECT)
                .user(user)
                .title("test1")
                .content("test1")
                .url("http://localhost:8080/boards/1")
                .people(5)
                .backend(3)
                .contact("contact")
                .dueDate(LocalDate.now())
                .startingDate(LocalDate.now())
                .periods("6")
                .onOffline("online")
                .build();

        Board board2 = Board.builder()
                .type(Board.Type.PROJECT)
                .user(user)
                .title("test2")
                .content("test2")
                .url("http://localhost:8080/boards/2")
                .people(5)
                .backend(3)
                .contact("contact")
                .dueDate(LocalDate.now())
                .startingDate(LocalDate.now())
                .periods("6")
                .onOffline("online")
                .build();

        Board board3 = Board.builder()
                .type(Board.Type.PROJECT)
                .user(user)
                .title("test3")
                .content("test3")
                .url("http://localhost:8080/boards/3")
                .people(5)
                .backend(3)
                .contact("contact")
                .dueDate(LocalDate.of(2023, 1, 13))
                .startingDate(LocalDate.now())
                .periods("6")
                .onOffline("online")
                .build();

        Board board4 = Board.builder()
                .type(Board.Type.PROJECT)
                .user(user)
                .title("test4")
                .content("test4")
                .url("http://localhost:8080/boards/4")
                .people(5)
                .backend(3)
                .contact("contact")
                .dueDate(LocalDate.of(2023, 1, 14))
                .startingDate(LocalDate.now())
                .periods("6")
                .onOffline("online")
                .build();

        BoardTag boardTag1 = BoardTag.of(board, tag);
        BoardTag boardTag2 = BoardTag.of(board2, tag2);
        BoardTag boardTag3 = BoardTag.of(board3, tag3);
        BoardTag boardTag4 = BoardTag.of(board4, tag);
        board.getBoardTagList().add(boardTag1);
        board2.getBoardTagList().add(boardTag1);
        board3.getBoardTagList().add(boardTag2);
        board4.getBoardTagList().add(boardTag3);
        board4.getBoardTagList().add(boardTag4);


        boardRepository.saveAll(List.of(board, board2, board3, board4));

        String token1 = jwtTokenProvider.generateToken(data.getEmail(), "ROLE_MEMBER").getAccessToken();
        String token2 = jwtTokenProvider.generateToken(data2.getEmail(), "ROLE_MEMBER").getAccessToken();

        System.out.println("user 1 : " + token1);
        System.out.println("user 2 : " + token2);
    }

}
