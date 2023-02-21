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
public class ApplicationServiceImpl implements ApplicationService{

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

        checkPositionAvailability(board, position);
        checkDuplicatedByUserIdAndBoardId(userId, boardId);

        Notification notification = Notification.of(Notification.Type.NEW_APPLICATION, board.getUser(), board.getUrl());

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
        Application findApply = findVerifiedApplication(applicationId);

        Board board = boardService.verifyBoardById(findApply.getBoard().getId());

        findApply.checkApplicationWriter(userId);
        checkPositionAvailability(board, findApply.getPosition());
//        findApply.setAccepted(true);
        findApply.setStatus(Application.Status.ACCEPTED);

        // 수락을 눌렀을 때, 지원자의 각 포지션 카운트 증가 후 Board 테이블에 저장.
        int curBack = board.getCurBackend();
        int curFront = board.getCurFrontend();
        int curDesign = board.getCurDesigner();
        int curEtc = board.getCurEtc();

        switch (findApply.getPosition()) {
            case "backend":
                board.setCurBackend(++curBack);
                break;
            case "frontend":
                board.setCurFrontend(++curFront);
                break;
            case "designer":
                board.setCurDesigner(++curDesign);
                break;
            case "etc":
                board.setCurEtc(++curEtc);
                break;
        }

        boardRepository.save(board);

        Application application = applicationRepository.save(findApply);
        Notification notification = Notification.of(Notification.Type.APPLICATION_ACCEPT, findApply.getUser(), board.getUrl());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));

        return application;
    }

    @Override
    @Transactional
    public void deny(long applicationId, long userId) {
        Application findApply = findVerifiedApplication(applicationId);
        findApply.checkApplicationWriter(userId);

        // 거절을 눌렀을 때, 지원자의 각 포지션 카운트 감소 후 Board 테이블에 저장.
//        int curBack = board.getCurBackend();
//        int curFront = board.getCurFrontend();
//        int curDesign = board.getCurDesigner();
//        int curEtc = board.getCurEtc();
//
//        if (findApply.getPosition().equals("backend") && curBack > 0) board.setCurBackend(--curBack);
//        else if(findApply.getPosition().equals("frontend") && curFront > 0) board.setCurFrontend(--curFront);
//        else if(findApply.getPosition().equals("designer") && curDesign > 0) board.setCurDesigner(--curDesign);
//        else if(findApply.getPosition().equals("etc") && curEtc > 0) board.setCurEtc(--curEtc);

//        boardRepository.save(board);

        findApply.setStatus(Application.Status.DENIED);
        applicationRepository.save(findApply);

        Notification notification = Notification.of(Notification.Type.APPLICATION_DENIED, findApply.getUser(), findApply.getBoard().getUrl());

        eventPublisher.publishEvent(new NotificationEvent(this, notification));

    }

    public Application findVerifiedApplication(long applicationId) {
        return applicationRepository.findById(applicationId).orElseThrow(
                () -> new CustomRuntimeException(ExceptionCode.APPLICATION_NOT_FOUND)
        );
    }

    public Application findUserIdApplication(long userId) {
        return applicationRepository.findById(userId).orElseGet(
                Application::new
        );
    }

    public void checkDuplicatedByUserIdAndBoardId(long userId, long boardId) {
        if (applicationRepository.findByUserIdAndBoardId(userId, boardId).isPresent()) {
            throw new CustomRuntimeException(ExceptionCode.APPLICATION_DUPLICATED);
        }
    }

    public void checkPositionAvailability(Board board, String position) {
        if (!board.isPositionAvailable(position)) {
            throw new CustomRuntimeException(ExceptionCode.APPLICATION_POSITION_UNAVAILABLE);
        }
    }
}
