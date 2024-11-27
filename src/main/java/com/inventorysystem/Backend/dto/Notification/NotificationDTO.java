package com.inventorysystem.Backend.dto.Notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {

    private Long id;                 // Unique identifier for the notification
    private String message;          // The message/content of the notification
    private Long articleId;          // The associated article ID
    private String name;             // The name of the article associated with the notification
    private boolean isRead;          // Whether the notification has been read
    private LocalDateTime dateTime;  // The timestamp of when the notification was created

    // This method can be used to get the article name instead of directly accessing the 'name' attribute
    public String getArticleName() {
        return name;
    }
}
