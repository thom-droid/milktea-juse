package com.example.juse.helper.utils;

import com.example.juse.board.entity.Board;
import com.example.juse.notification.entity.Notification;
import com.example.juse.user.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void createBoardRedirectUri() {
        String expected = "https://chicken-milktea-juse.com/boards/1";
        String requestUrl = "https://chicken-milktea-juse.com";
        Long id = 1L;
        String actual = StringUtils.createBoardRedirectUri(requestUrl, id);
        assertEquals(expected, actual);
    }
}