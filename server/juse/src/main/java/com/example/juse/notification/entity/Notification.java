package com.example.juse.notification.entity;

import com.example.juse.audit.Auditing;
import com.example.juse.board.entity.Board;
import com.example.juse.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "NOTIFICATIONS")
public class Notification extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "RECEIVER_ID")
    private User receiver;

    private String message;
    private String relatedURL;
    private boolean isRead;
    private Long boardId;

    public static Notification of(Type type, User receiver, Board board) {
        return Notification.buildCommonProperties(type, board)
                .receiver(receiver)
                .build();
    }

    public static Notification of(Type type, Board board) {
        return Notification.buildCommonProperties(type, board)
                .receiver(board.getUser())
                .build();
    }

    private static NotificationBuilder buildCommonProperties(Type type, Board board) {
        String message = createNotificationMessage(type, board.getTitle());
        return Notification.builder()
                .type(type)
                .relatedURL(board.getUrl())
                .message(message)
                .boardId(board.getId());
    }

    @Getter
    public enum Type {
        NEW_REPLY(1, "새 댓글이 달렸습니다."),
        NEW_APPLICATION(2,"새 지원자가 있습니다."),
        APPLICATION_ACCEPT(3,"지원이 수락되었습니다."),
        APPLICATION_DENIED(4, "지원이 거절되었습니다.");

        final String message;
        final int code;

        Type(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }

    private static String createNotificationMessage(Notification.Type type, String boardTitle) {
        if (boardTitle.isBlank()) {
            return null;
        }

        String trimmedTitle = trimTitle(boardTitle);
        if (type.getCode() <= 2) {
            return "게시글 " + trimmedTitle + "에 " + type.getMessage();
        }
        if (type.getCode() == 3 || type.getCode() == 4) {
            return "게시글 " + trimmedTitle + "에 대한 " + type.getMessage();
        }
        return null;
    }

    private static String trimTitle(String str) {
        if (str.length() <= 6) {
            return str;
        }

        return str.substring(0, 6).concat("...");
    }
}
