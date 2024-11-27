package com.inventorysystem.Backend.service;

import com.inventorysystem.Backend.dto.Notification.NotificationDTO;
import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getUnreadNotifications();
    void markAsRead(Long id); // Marks notification by ID

    List<NotificationDTO> getUnreadExpiryNotifications();
    void markAsRead(String name); // Marks notification by name (expiry)
}
