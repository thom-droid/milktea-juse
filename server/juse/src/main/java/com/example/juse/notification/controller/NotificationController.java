package com.example.juse.notification.controller;

import com.example.juse.dto.MultiResponseDto;
import com.example.juse.dto.Pagination;
import com.example.juse.notification.dto.NotificationResponseDto;
import com.example.juse.notification.entity.Notification;
import com.example.juse.notification.mapper.NotificationMapper;
import com.example.juse.notification.service.NotificationService;
import com.example.juse.security.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;


    @GetMapping(value = "/event-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter createEventStream(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                        @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId) {
        String userUUID = principalDetails.getSocialUser().getUser().getUuid();

        return notificationService.createEventStream(userUUID, lastEventId);

    }

    @GetMapping("/notifications")
    public ResponseEntity<MultiResponseDto<NotificationResponseDto>> getNotificationList(
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "isRead", defaultValue = "false") Boolean isRead

    ) {
        Long userId = principalDetails.getSocialUser().getUser().getId();

        log.info("is read : {} ", isRead);

        Pageable pageable = PageRequest.of(page - 1, 5, Sort.by("createdAt").descending());

        Page<Notification> responsePage = notificationService.getNotificationList(userId, isRead, pageable);
        List<Notification> content = responsePage.getContent();
        List<NotificationResponseDto> responseDtoList = notificationMapper.mapToDtoListFromEntityList(content);

        responseDtoList.forEach(responseDto -> log.info("notification msg : {}", responseDto.getMessage()));

        Pagination pagination = Pagination.of(responsePage);

        return new ResponseEntity<>(new MultiResponseDto<>(responseDtoList, pagination), HttpStatus.OK);
    }
}
