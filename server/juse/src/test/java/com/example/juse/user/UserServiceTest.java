package com.example.juse.user;

import com.example.juse.social.entity.SocialUser;
import com.example.juse.tag.entity.Tag;
import com.example.juse.tag.service.TagService;
import com.example.juse.tag.service.UserTagService;
import com.example.juse.user.entity.User;
import com.example.juse.user.mapper.UserMapper;
import com.example.juse.user.repository.UserRepository;
import com.example.juse.user.service.StorageService;
import com.example.juse.user.service.UserService;
import com.example.juse.user.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@ContextConfiguration(classes = {UserServiceImpl.class})
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    TagService tagService;

    @MockBean
    UserTagService userTagService;

    @MockBean
    UserMapper userMapper;

    @MockBean
    StorageService storageService;

    @Test
    public void dependencyTest() {

    }

    @Test
    public void givenMultipartFile_whenUserCreated_thenSuccess() throws IOException {

        byte[] image = new byte[]{1, 2, 3, 4};
        MockMultipartFile mockMultipartFile = new MockMultipartFile("test.jpg", "test.jpg","image/jpg", image);

        User testUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .img(mockMultipartFile.getName())
                .myImg(mockMultipartFile.getOriginalFilename())
                .socialUser(SocialUser.builder().email("test@gmail.com").build())
                .build();

        given(storageService.store(Mockito.any(MultipartFile.class))).willReturn("savedFileName");
        given(tagService.findByName(Mockito.anyString())).willReturn(Mockito.mock(Tag.class));
        given(userRepository.save(Mockito.any(User.class))).willReturn(testUser);

        //when
        User createdUser = userService.createUser(testUser, mockMultipartFile);

        //then
        assertEquals(testUser.getImg(), createdUser.getImg());
        assertEquals(testUser.getMyImg(), createdUser.getMyImg());

    }


}
