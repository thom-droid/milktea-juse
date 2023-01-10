package com.example.juse.notification.repository;

import com.example.juse.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n " +
            "FROM Notification n " +
            "WHERE n.receiver.id = :receiverId " +
            "AND n.isRead = :isRead")
    Page<Notification> findByReceiverIdAndIsRead(@Param("receiverId") Long receiverId,
                                                 @Param("isRead") Boolean isRead,
                                                 Pageable pageable);

}
