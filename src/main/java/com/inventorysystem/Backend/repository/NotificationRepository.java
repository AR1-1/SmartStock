package com.inventorysystem.Backend.repository;

import com.inventorysystem.Backend.dto.Notification.NotificationDTO;
import com.inventorysystem.Backend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Fetch notifications by read status
    List<Notification> findByIsRead(boolean isRead);

    // Count unread notifications for a specific article
    @Query(value = "SELECT count(n) FROM Notification n WHERE n.articleId = :articleId AND n.isRead = false")
    Long countNotificationByArticleId(@Param("articleId") Long articleId);

    // Fetch unread notifications with article name (Join with Article)
    @Query("SELECT new com.inventorysystem.Backend.dto.Notification.NotificationDTO(" +
            "n.id, n.message, n.articleId, a.name, n.isRead, n.dateTime) " +
            "FROM Notification n " +
            "JOIN Article a ON n.articleId = a.articleId " +
            "WHERE n.isRead = false")
    List<NotificationDTO> findUnreadNotificationsWithArticleNames();

    // Check if a notification exists for a specific article and message
    boolean existsByArticleIdAndMessage(Long articleId, String message);

    // Fetch only unread notifications
    List<Notification> findByIsReadFalse();

    // Find notifications by name (e.g., for expiry notifications)
    List<Notification> findByName(String name);

    // Fetch unread notifications with a specific name (e.g., expiry)
    @Query("SELECT n FROM Notification n WHERE n.isRead = false AND n.name = :name")
    List<Notification> findUnreadNotificationsByName(@Param("name") String name);

    // Fetch unread notifications containing a specific keyword in their name
    List<Notification> findByIsReadFalseAndNameContaining(String name);
}
