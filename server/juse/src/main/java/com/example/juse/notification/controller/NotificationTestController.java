package com.example.juse.notification.controller;

import com.example.juse.notification.service.NotificationService;
import com.example.juse.security.jwt.JwtTokenProvider;
import com.example.juse.security.oauth.PrincipalDetails;
import com.example.juse.user.entity.User;
import com.example.juse.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;


/** only used for test */
@RequiredArgsConstructor
@RequestMapping("/notification-test")
@Controller
public class NotificationTestController {

    private final NotificationService notificationService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @GetMapping(value = "/event-stream/{uuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public @ResponseBody SseEmitter testConnection(@PathVariable("uuid") String userUUID,
                                                   @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {

        return notificationService.createEventStream(userUUID, lastEventId);

    }

    @GetMapping("/login")
    public String testLoginForm() {
        return "index";
    }

    @PostMapping("/login")
    public String testLogin(String email, HttpServletResponse response) {
        User user = userRepository.findByEmail(email);
        String userUUID = user.getUuid();

        response.addCookie(new Cookie("uuid", userUUID));

        return "sse-test";
    }

//    @GetMapping("/index")
    public String testIndex(@AuthenticationPrincipal PrincipalDetails principalDetails,
                            HttpServletResponse response) {

        User user = principalDetails.getSocialUser().getUser();
        String userUUID = user.getUuid();
        String token = jwtTokenProvider.generateToken(user.getEmail(), "ROLE_MEMBER").getAccessToken();

        response.setHeader("Auth", token);
        response.setHeader("user-uuid", userUUID);
        response.setHeader("nickname", user.getNickname());

        return "sseTest";
    }
}
