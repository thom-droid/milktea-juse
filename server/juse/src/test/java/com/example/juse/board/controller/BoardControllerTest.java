package com.example.juse.board.controller;

import com.example.juse.JuseApplicationTests;
import com.example.juse.board.dto.BoardRequestDto;
import com.example.juse.board.entity.Board;
import com.example.juse.board.repository.BoardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
class BoardControllerTest extends JuseApplicationTests {

    @Autowired
    private Gson gson;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardRepository boardRepository;


    @WithMockUser
    @Test
    void givenRequestDto_whenPostRequest_thenRequestURLReturned() throws Exception {

        //given
        BoardRequestDto.Post postDto = BoardRequestDto.Post.builder()
                .title("board1")
                .content("board1")
                .people(13)
                .contact("online")
                .dueDate(LocalDate.of(2023, 1, 2))
                .startingDate(LocalDate.of(2023, 1, 3))
                .periods("6")
                .onOffline("online")
                .type(Board.Type.PROJECT)
                .tagList(List.of("java"))
                .build();

        String request = objectMapper.writeValueAsString(postDto);

        //when
        ResultActions resultActions = mockMvc
                .perform(post("/boards")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("Auth", accessToken)
                );

        //then
        resultActions.andExpect(status().isCreated());
        int size = boardRepository.findAll().size();
        String expectedUrl = resultActions.andReturn().getRequest().getRequestURL().append("/").append(size).toString();
        resultActions.andExpect(jsonPath("$.data.url").value(expectedUrl));

    }
}