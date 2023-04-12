package com.example.juse.notification.repository;

import com.example.juse.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n " +
            "FROM Notification n " +
            "WHERE n.receiver.id = :receiverId " +
            "AND n.isRead = :isRead")
    Page<Notification> findByReceiverIdAndIsRead(@Param("receiverId") Long receiverId,
                                                 @Param("isRead") Boolean isRead,
                                                 Pageable pageable);

    @Query(nativeQuery = true,
            value = "SELECT n.id, n.message, n.relatedurl, n.receiver_id, n.is_read, n.type, n.created_at, n.modified_at, n.board_id " +
                    "FROM notifications n " +
                    "WHERE n.receiver_id = :receiverId AND n.is_read = false " +
                    "ORDER BY n.created_at DESC " +
                    "LIMIT 5 "
    )
    List<Notification> findTop5ByReceiverIdOrderByCreatedAtDesc(@Param("receiverId") Long receiverId);
}
