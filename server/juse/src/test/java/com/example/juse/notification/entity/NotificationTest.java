package com.example.juse.notification.entity;

import com.example.juse.board.entity.Board;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void notificationMessageTest() {
        Board board = Board.builder().title("가나다라마바사아자카타파하").build();
        String expected = "게시글 가나다라마바...에 새 댓글이 달렸습니다.";
        Notification notification = Notification.of(Notification.Type.NEW_REPLY, board);
        String actual = notification.getMessage();
        assertEquals(expected, actual);
    }
}