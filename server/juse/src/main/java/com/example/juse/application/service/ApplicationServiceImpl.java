package com.example.juse.application.service;

import com.example.juse.application.entity.Application;
import com.example.juse.application.repository.ApplicationRepository;
import com.example.juse.board.entity.Board;
import com.example.juse.board.repository.BoardRepository;
import com.example.juse.board.service.BoardService;
import com.example.juse.event.NotificationEvent;
import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import com.example.juse.notification.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final BoardRepository boardRepository;
    private final BoardService boardService;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Application create(Application mappedObj) {
        long userId = mappedObj.getUser().getId();
        long boardId = mappedObj.getBoard().getId();
        Board board = boardService.verifyBoardById(boardId);
        String position = mappedObj.getPosition();

        checkDuplicatedApplication(board, userId);
        checkPositionAvailability(board, position);

        Notification notification = Notification.of(Notification.Type.NEW_APPLICATION, board);
        Application application = applicationRepository.save(mappedObj);

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
        return application;
    }

    @Override
    public Application update(Application mappedObj) {
        return null;
    }

    @Override
    @Transactional
    public Application accept(long applicationId, long userId) {
        Application foundApplication = findVerifiedApplication(applicationId);
        Board board = boardService.verifyBoardById(foundApplication.getBoard().getId());

        foundApplication.verifyBoardWriter(userId);
        checkPositionAvailability(board, foundApplication.getPosition());
        foundApplication.setStatusAsAccepted();
        board.incrementPositionCount(foundApplication.getPosition());

        boardRepository.save(board);

        Application application = applicationRepository.save(foundApplication);
        Notification notification = Notification.of(Notification.Type.APPLICATION_ACCEPT, application.getUser(), board);

        eventPublisher.publishEvent(new NotificationEvent(this, notification));

        return application;
    }

    @Override
    @Transactional
    public void deny(long applicationId, long userId) {
        Application foundApplication = findVerifiedApplication(applicationId);
        foundApplication.verifyBoardWriter(userId);

        foundApplication.setStatusAsDenied();
        applicationRepository.save(foundApplication);

        Notification notification =
                Notification.of(
                        Notification.Type.APPLICATION_DENIED,
                        foundApplication.getUser(),
                        foundApplication.getBoard());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));
    }

    public Application findVerifiedApplication(long applicationId) {
        return applicationRepository.findById(applicationId).orElseThrow(
                () -> new CustomRuntimeException(ExceptionCode.APPLICATION_NOT_FOUND)
        );
    }

    public void checkPositionAvailability(Board board, String position) {
        if (!board.isPositionAvailable(position)) {
            throw new CustomRuntimeException(ExceptionCode.APPLICATION_POSITION_UNAVAILABLE);
        }
    }

    public void checkDuplicatedApplication(Board board, Long applicantId) {
        if (board.checkDuplicatedApplicant(applicantId)) {
            throw new CustomRuntimeException(ExceptionCode.APPLICATION_DUPLICATED);
        }
    }
    // Todo 페이지 새로고침 시 이미지 경로 못 불러옴, sse 못불러옴 (useEffect 떄문에)
    // 중간에 서버 꺼졌을 때 쿠키 없애고 새로고침 시켜야함
    // 다른 기능들 다 테스트
    // 알림 읽음 처리

}
