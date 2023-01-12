package com.example.juse.notification.entity;

import com.example.juse.audit.Auditing;
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

    public static Notification of(Type type, User receiver, String relatedURL) {
        return Notification.builder()
                .type(type)
                .receiver(receiver)
                .relatedURL(relatedURL)
                .message(type.getMessage())
                .build();
    }

    @Getter
    public enum Type {

        NEW_REPLY("새 댓글이 달렸습니다"),
        NEW_APPLICATION("새 지원자가 있습니다"),
        APPLICATION_ACCEPT("지원이 수락되었습니다"),
        APPLICATION_DENIED("지원이 거절되었습니다");

        final String message;

        Type(String message) {
            this.message = message;
        }
    }

}
