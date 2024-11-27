package com.inventorysystem.Backend.service.imp;

import com.inventorysystem.Backend.model.Article;
import com.inventorysystem.Backend.model.Notification;
import com.inventorysystem.Backend.repository.ArticleRepository;
import com.inventorysystem.Backend.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableScheduling
public class SaveNotificationScheduler {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Adjusted @Scheduled with fixedRate of 5 seconds (or your preferred interval)
    @Scheduled(fixedRate = 5000)
    public void SaveNotification() {

        // Check for articles with low stock (threshold: 3)
        List<Article> articleList = articleRepository.findArticlesByStockThreshold(3);
        System.out.println("Item count for low stock check: " + articleList.size());

        // Handle low stock notifications
        articleList.forEach(article -> {
            // Check if a low stock notification already exists for this article
            if (notificationRepository.countNotificationByArticleId(article.getArticleId()) == 0) {
                Notification stockNotification = new Notification();
                stockNotification.setName(article.getName());
                stockNotification.setMessage("Stock is getting low for item: " + article.getName());
                stockNotification.setArticleId(article.getArticleId());
                stockNotification.setDateTime(LocalDateTime.now());
                stockNotification.setRead(false);  // Set the notification as unread
                notificationRepository.save(stockNotification);
            }
        });

        // Calculate expiry threshold (7 days from today)
        LocalDate expiryThreshold = LocalDate.now().plusDays(7);

        // Check for articles nearing expiry (e.g., within the next 7 days)
        List<Article> articlesNearingExpiry = articleRepository.findArticlesByExpiryDateThreshold(expiryThreshold);
        System.out.println("Item count for expiry check: " + articlesNearingExpiry.size());

        // Handle expiry notifications
        articlesNearingExpiry.forEach(article -> {
            // Check if a nearing expiry notification already exists for this article
            boolean notificationExists = notificationRepository.existsByArticleIdAndMessage(
                    article.getArticleId(),
                    "Item '" + article.getName() + "' is nearing expiry on " + article.getExpiryDate()
            );
            if (!notificationExists) {
                Notification expiryNotification = new Notification();
                expiryNotification.setName(article.getName());
                expiryNotification.setMessage("Item '" + article.getName() + "' is nearing expiry on " + article.getExpiryDate());
                expiryNotification.setArticleId(article.getArticleId());
                expiryNotification.setDateTime(LocalDateTime.now());
                expiryNotification.setRead(false);  // Set the notification as unread
                notificationRepository.save(expiryNotification);
            }
        });
    }
}
