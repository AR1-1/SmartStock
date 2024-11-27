package com.inventorysystem.Backend.service.imp;

import com.inventorysystem.Backend.dto.Notification.NotificationDTO;
import com.inventorysystem.Backend.model.Notification;
import com.inventorysystem.Backend.repository.NotificationRepository;
import com.inventorysystem.Backend.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImp implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<NotificationDTO> getUnreadNotifications() {
        List<Notification> unreadNotifications = notificationRepository.findByIsRead(false);
        return unreadNotifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getArticleId(),
                        notification.getName(),
                        notification.isRead(),
                        notification.getDateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + id));
        notification.setRead(true); // Properly sets the notification as read
        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationDTO> getUnreadExpiryNotifications() {
        // Fetch unread notifications related to expiry using the repository method
        List<Notification> unreadExpiryNotifications = notificationRepository.findByIsReadFalseAndNameContaining("name");
        return unreadExpiryNotifications.stream()
                .map(notification -> new NotificationDTO(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getArticleId(),
                        notification.getName(),
                        notification.isRead(),
                        notification.getDateTime()))
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String name) {
        // Mark notifications related to expiry by name
        List<Notification> notifications = notificationRepository.findByName(name);
        if (notifications.isEmpty()) {
            throw new RuntimeException("Expiry notifications not found for name: " + name);
        }

        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
