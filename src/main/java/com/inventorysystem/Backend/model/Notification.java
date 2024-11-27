package com.inventorysystem.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;

    private Long articleId;

    private LocalDateTime created_at;

   // private Article article;
    private String name;
    private boolean isRead;
    private LocalDateTime dateTime;
    public String getArticleName() {
        return name;
    }


}