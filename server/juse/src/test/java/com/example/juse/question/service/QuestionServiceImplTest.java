package com.example.juse.question.service;

import com.example.juse.TestDBInstance;
import com.example.juse.question.dto.QuestionRequestDto;
import com.example.juse.question.entity.Question;
import com.example.juse.question.mapper.QuestionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
@TestPropertySource(locations = {"/application.properties", "/application-oauth-local.properties"})
@Import(TestDBInstance.class)
@SpringBootTest
class QuestionServiceImplTest  {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionMapper questionMapper;

    @Test
    void givenQuestionPostDto_whenQeustionServiceInvoked_thenNotificationIsSent() {

        QuestionRequestDto.Post post = QuestionRequestDto.Post.builder().
                userId(2L).
                boardId(1L).
                content("백슉먹자해짜나")
                .build();

        Question question = questionMapper.toEntityFrom(post);

        assertDoesNotThrow(() -> questionService.create(question));

    }
}