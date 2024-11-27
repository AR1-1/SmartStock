package com.inventorysystem.Backend.controller;

import com.inventorysystem.Backend.dto.article.ArticleCreationDTO;
import com.inventorysystem.Backend.dto.article.ArticleDTO;
import com.inventorysystem.Backend.dto.article.ArticleUpdateDTO;
import com.inventorysystem.Backend.dto.article.ArticlesPageDTO;
import com.inventorysystem.Backend.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
@CrossOrigin
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    // Method to create a new article
    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(@RequestBody ArticleCreationDTO article) {
        try {
            ArticleDTO createdArticle = articleService.createArticle(article);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
        } catch (Exception e) {
            // Log the exception (use a proper logging framework in real applications)
            System.err.println("Error creating article: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Method to get a list of all articles, with pagination and optional search criteria
    @GetMapping
    public ResponseEntity<ArticlesPageDTO> getAllArticles(
            @RequestParam(name = "providerId", required = false) Long providerId,
            @RequestParam(name = "searchCriteria", required = false) String criteria,
            @RequestParam(name = "page") Integer page,
            @RequestParam(name = "pageSize") Integer pageSize
    ) {
        if (page < 1 || pageSize < 1) {
            // Return BAD_REQUEST if page or pageSize are invalid
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        try {
            ArticlesPageDTO articlesPage = articleService.getAllArticles(providerId, criteria, page, pageSize);
            return ResponseEntity.status(HttpStatus.OK).body(articlesPage);
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error fetching articles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Method to get a specific article by its ID
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable Long id) {
        try {
            ArticleDTO article = articleService.getArticleById(id);
            return ResponseEntity.status(HttpStatus.OK).body(article);
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error fetching article by id: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Method to update an existing article
    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(@PathVariable Long id, @RequestBody ArticleUpdateDTO articleData) {
        try {
            ArticleDTO updatedArticle = articleService.updateArticle(id, articleData);
            return ResponseEntity.status(HttpStatus.OK).body(updatedArticle);
        } catch (Exception e) {
            // Log the exception
            System.err.println("Error updating article: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
