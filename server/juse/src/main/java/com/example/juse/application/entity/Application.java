package com.example.juse.application.entity;

import com.example.juse.audit.Auditing;
import com.example.juse.board.entity.Board;
import com.example.juse.exception.CustomRuntimeException;
import com.example.juse.exception.ExceptionCode;
import com.example.juse.user.entity.User;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "APPLICATIONS")
@ToString
public class Application extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String position;

    @ManyToOne
    @JoinColumn(name = "BOARD_ID")
    private Board board;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Setter
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private Status status = Status.ON_WAIT;

    @Getter
    public enum Status {

        ON_WAIT("지원 후 대기 중"),
        ACCEPTED("수락됨"),
        DENIED("지원 거절됨");

        private final String description;

        Status(String description) {
            this.description = description;
        }

    }

    public void checkApplicationWriter(long userId) {
        if (this.getBoard().getUser().getId() != userId) {
            throw new CustomRuntimeException(ExceptionCode.APPLICATION_INVALID_REQUEST);
        }
    }
}