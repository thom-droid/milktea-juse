package com.example.juse.question.service;

import com.example.juse.board.entity.Board;
import com.example.juse.board.service.BoardService;
import com.example.juse.event.NotificationEvent;
import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import com.example.juse.notification.entity.Notification;
import com.example.juse.question.entity.Question;
import com.example.juse.question.mapper.QuestionMapper;
import com.example.juse.question.repository.QuestionRepository;
import com.example.juse.user.entity.User;
import com.example.juse.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService{

    private final BoardService boardService;
    private final UserService userService;
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    private final ApplicationEventPublisher eventPublisher;


    @Override
    @Transactional
    public Question create(Question post) {
        long boardId = post.getBoard().getId();
        long userId = post.getUser().getId();

        Board board = boardService.verifyBoardById(boardId);
        User user = userService.verifyUserById(userId);

        post.addBoard(board);
        post.addUser(user);

        Notification notification = Notification.of(Notification.Type.NEW_REPLY, board.getUser(), board.getUrl());

        log.info("question saved. the event is ready to be sent");

        Question savedQuestion = questionRepository.save(post);

        eventPublisher.publishEvent(new NotificationEvent(this, notification));

        return savedQuestion;
    }

    @Override
    public Question update(Question patch) {
        Question question = verifyQuestionById(patch.getId());
        long userId = patch.getUser().getId();

        if (!question.isCreatedBy(userId)) {
            throw new CustomRuntimeException(ExceptionCode.QUESTION_WRITER_NOT_MATCHED);
        }

        questionMapper.updateEntityFromSource(question, patch);

        return questionRepository.save(question);
    }

    @Override
    public void delete(long questionId, long userId) {
        Question question = verifyQuestionById(questionId);

        if (!question.isCreatedBy(userId)) {
            throw new CustomRuntimeException(ExceptionCode.QUESTION_WRITER_NOT_MATCHED);
        }

        questionRepository.delete(question);
    }

    @Override
    public Question verifyQuestionById(long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(
                        () -> new CustomRuntimeException(ExceptionCode.QUESTION_NOT_FOUND)
                );
    }

}
