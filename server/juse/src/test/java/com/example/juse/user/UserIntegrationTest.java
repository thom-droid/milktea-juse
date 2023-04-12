package com.example.juse.user;

import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.social.entity.SocialUser;
import com.example.juse.social.repository.SocialUserRepository;
import com.example.juse.user.controller.UserController;
import com.example.juse.user.dto.UserRequestDto;
import com.example.juse.user.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@AutoConfigureMockMvc
@SpringBootTest(
        properties = "jwt.secret=${random.uuid}",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class UserIntegrationTest {

    @Autowired
    UserController userController;

    @Autowired
    SocialUserRepository socialUserRepository;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserMapper userMapper;

    @Autowired
    MockMvc mockMvc;

    String accessToken;

    @LocalServerPort
    int port;

    @Autowired
    ObjectMapper objectMapper;

    final String POST_API_URL = "http://localhost:" + port + "/users/join";

    @BeforeEach
    public void setup() {
        SocialUser socialUser = SocialUser.builder().email("test1@gmail.com").name("test").build();

        socialUserRepository.save(socialUser);

        //token
        accessToken = jwtTokenProvider.generateToken(socialUser.getEmail(), "USER_ROLE").getAccessToken();
    }

    @Test
    public void givenMockMultipartFileAndAccessToken_whenUserPost_thenSucceed() throws Exception {

        //given
        UserRequestDto.Post request = UserRequestDto.Post.builder()
                .email("test1@gmail.com")
                .nickname("nickname")
                .introduction("introduction")
                .socialUserId(1L)
                .build();

        String content = objectMapper.writeValueAsString(request);

        MockMultipartFile part1 =
                new MockMultipartFile("image", "image", MediaType.IMAGE_JPEG_VALUE, "image".getBytes());

        MockMultipartFile part2 =
                new MockMultipartFile("userPostDto", "", MediaType.APPLICATION_JSON_VALUE, content.getBytes());

        ResultActions resultActions = mockMvc.perform(multipart(POST_API_URL)
                .file(part1)
                .file(part2)
                .header("Auth", accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
        );

        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").value("test1@gmail.com"))
                .andDo(print());

    }


}
