package com.example.juse.board.controller;

import com.example.juse.board.dto.BoardRequestDto;
import com.example.juse.board.entity.Board;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"testonly", "plain", "oauth"})
@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerTest {

    @Autowired
    Gson gson;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;


    @WithMockUser
    @Test
    void givenRequestDto_whenPostRequest_thenRequestURLReturned() throws Exception {

        BoardRequestDto.Post postDto = BoardRequestDto.Post.builder()
                .title("board1")
                .content("board1")
                .people(13)
                .contact("online")
                .dueDate(LocalDate.of(2023, 1, 2))
                .startingDate(LocalDate.of(2023, 1, 3))
                .period("6")
                .onOffline("online")
                .type(Board.Type.PROJECT)
                .tagList(List.of("java"))
                .build();

        String request = objectMapper.writeValueAsString(postDto);
        System.out.println(request);


        ResultActions resultActions = mockMvc
                .perform(post("/boards")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                );

        resultActions.andExpect(status().isCreated()).andDo(print());


    }
}