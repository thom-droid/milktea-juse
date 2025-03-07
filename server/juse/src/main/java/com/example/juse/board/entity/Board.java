package com.example.juse.board.entity;

import com.example.juse.application.entity.Application;
import com.example.juse.audit.Auditing;
import com.example.juse.bookmark.entity.Bookmark;
import com.example.juse.question.entity.Question;
import com.example.juse.tag.entity.BoardTag;
import com.example.juse.user.entity.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BOARDS")
public class Board extends Auditing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String url;
    private int backend;
    private int curBackend;
    private int frontend;
    private int curFrontend;
    private int designer;
    private int curDesigner;
    private int etc;
    private int curEtc;
    private int bookmarks;
    private int views;
    private int curPeople;

    @Column(nullable = false)
    private int people;

    @Column(nullable = false)
    private String contact;

    @Column(nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDate startingDate;

    @Column(nullable = false)
    private String periods;

    @Column(nullable = false)
    private String onOffline;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.OPENING;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Question> questionList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<Bookmark> bookmarkList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<BoardTag> boardTagList = new ArrayList<>();

//    @Builder.Default
//    @OneToMany(mappedBy = "board")
//    private List<Like> likeList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Application> applicationList = new ArrayList<>();

    public List<String> getTagNames() {
        return this.boardTagList.stream().map(boardTag -> boardTag.getTag().getName()).collect(Collectors.toList());
    }

    public int getLikedCount() {
        return this.bookmarkList.size();
    }

    @Getter
    public enum Type {

        PROJECT,
        STUDY,

    }

    @Getter
    public enum Status {

        OPENING,
        CLOSED

    }

    public void addUser(User user) {
        this.user = user;
        if (!this.user.getBoardList().contains(this)) {
            this.user.getBoardList().add(this);
        }
    }

    public boolean isCreatedBy(long userId) {
        return this.user.getId() == userId;
    }

    public boolean isBookmarkedBy(long userId) {
        return this.bookmarkList.stream().anyMatch(bookmark -> bookmark.getUser().getId() == userId);
    }

    public boolean isWriterLikedBy(long userId) {
        return this.user.isLikedBy(userId);
    }

    public boolean isPositionAvailable(String position) {
        if (this.type == Type.PROJECT) {
            if (position.equals("backend")) {
                return backend > curBackend;
            }
            if (position.equals("frontend")) {
                return frontend > curFrontend;
            }
            if (position.equals("designer")) {
                return designer > curDesigner;
            }
            if (position.equals("etc")) {
                return etc > curEtc;
            }
        } else {
            return people > curPeople;
        }
        return false;
    }

    public void incrementPositionCount(String position) {
        if (position.equals("backend")) {
            curBackend++;
        }
        if (position.equals("frontend")) {
            curFrontend++;
        }
        if (position.equals("designer")) {
            curDesigner++;
        }
        if (position.equals("etc")) {
            curEtc++;
        }
        if (position.equals("people")) {
            curPeople++;
        }
    }

    public void incrementBookmarkCount() {
        this.bookmarks++;
    }

    public boolean checkDuplicatedApplicant(Long applicantId) {
        return this.applicationList.stream().anyMatch(application -> Objects.equals(application.getUser().getId(), applicantId));
    }
}