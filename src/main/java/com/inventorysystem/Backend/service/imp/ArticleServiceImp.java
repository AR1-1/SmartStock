package com.inventorysystem.Backend.service.imp;

import com.inventorysystem.Backend.dto.article.ArticleCreationDTO;
import com.inventorysystem.Backend.dto.article.ArticleDTO;
import com.inventorysystem.Backend.dto.article.ArticleUpdateDTO;
import com.inventorysystem.Backend.dto.article.ArticlesPageDTO;
import com.inventorysystem.Backend.mapper.ArticleMapper;
import com.inventorysystem.Backend.model.Article;
import com.inventorysystem.Backend.model.Notification;
import com.inventorysystem.Backend.model.Stock;
import com.inventorysystem.Backend.repository.ArticleRepository;
import com.inventorysystem.Backend.repository.NotificationRepository;
import com.inventorysystem.Backend.repository.StockRepository;
import com.inventorysystem.Backend.repository.specifications.ArticleSpecifications;
import com.inventorysystem.Backend.service.ArticleService;
import com.inventorysystem.Backend.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImp implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StockRepository stockRepository;  // Repository for stock operations

    @Autowired
    private StockService stockService;  // StockService for stock-related business logic

    @Override
    @Transactional
    public ArticleDTO createArticle(ArticleCreationDTO article) {
        Long newArticleId = articleRepository.createArticle(
                article.getName(),
                article.getBrand(),
                article.getStock(),
                article.getPurchasePrice(),
                article.getSalePrice(),
                article.getWeight(),
                article.getProviderId(),
                article.getCategoryId()
        );
        return getArticleById(newArticleId);
    }

    @Override
    @Transactional
    public ArticlesPageDTO getAllArticles(Long providerId, String criteria, Integer page, Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("articleId").descending());
        Page<Article> articlePage = resolveArticlePage(providerId, criteria, pageable);

        List<ArticleDTO> articles = articlePage.getContent().stream()
                .map(articleMapper::articleToDTO)
                .collect(Collectors.toList());

        return buildArticlesPageResponse(articlePage, articles);
    }

    @Override
    @Transactional
    public ArticleDTO getArticleById(Long id) {
        Article foundArticle = articleRepository.getArticleById(id);
        return articleMapper.articleToDTO(foundArticle);
    }

    @Override
    @Transactional
    public ArticleDTO updateArticle(Long articleId, ArticleUpdateDTO articleData) {
        Article foundArticle = validateAndUpdateArticle(articleId, articleData);

        // Trigger notification if stock is low after update
        if (foundArticle.getStock() <= 5) {
            triggerLowStockNotification(foundArticle);
        }

        return getArticleById(foundArticle.getArticleId());
    }

    // New method to handle selling stock
    @Transactional
    public boolean sellStock(Long articleId, int quantityToSell) {
        Article article = articleRepository.getArticleById(articleId);
        if (article == null) {
            throw new IllegalArgumentException("Article not found.");
        }

        // Check if sufficient stock is available
        if (article.getStock() < quantityToSell) {
            return false;  // Not enough stock
        }

        // Update stock by reducing the quantity
        article.setStock(article.getStock() - quantityToSell);
        articleRepository.updateArticle(
                article.getArticleId(),
                article.getName(),
                article.getBrand(),
                article.getStock(),
                article.getPurchasePrice(),
                article.getSalePrice(),
                article.getWeight(),
                article.getProviderId(),
                article.getCategoryId()
        );

        // Trigger low stock notification if the stock goes below threshold
        if (article.getStock() <= 5) {
            triggerLowStockNotification(article);
        }

        return true;  // Stock sold successfully
    }

    // Method to fetch articles nearing expiry
    public List<Article> getArticlesNearingExpiry(int thresholdDays) {
        LocalDate currentDate = LocalDate.now();
        return articleRepository.findAll().stream()
                .filter(article -> article.getExpiryDate() != null)
                .filter(article -> {
                    long daysToExpiry = article.getExpiryDate().toEpochDay() - currentDate.toEpochDay();
                    return daysToExpiry <= thresholdDays && daysToExpiry >= 0;
                })
                .collect(Collectors.toList());
    }

    private Page<Article> resolveArticlePage(Long providerId, String criteria, Pageable pageable) {
        if (providerId == null && (criteria == null || criteria.isEmpty())) {
            return articleRepository.findAll(pageable);
        } else if (providerId != null && (criteria == null || criteria.isEmpty())) {
            return articleRepository.findAllArticlesByProvider(providerId, pageable);
        } else if (providerId != null) {
            return articleRepository.findAllArticlesByProviderAndTerm(providerId, criteria, pageable);
        } else {
            return articleRepository.findAll(ArticleSpecifications.searchArticles(criteria), pageable);
        }
    }

    private ArticlesPageDTO buildArticlesPageResponse(Page<Article> articlePage, List<ArticleDTO> articles) {
        ArticlesPageDTO response = new ArticlesPageDTO();
        response.setPage(articlePage.getNumber() + 1);
        response.setPageSize(articlePage.getSize());
        response.setTotalRecords(articlePage.getTotalElements());
        response.setTotalPages(Math.max(articlePage.getTotalPages(), 1));
        response.setArticles(articles);
        return response;
    }

    private Article validateAndUpdateArticle(Long articleId, ArticleUpdateDTO articleData) {
        Article foundArticle = articleRepository.getArticleById(articleId);

        if (!articleData.getName().equalsIgnoreCase(foundArticle.getName()) &&
                articleRepository.findByName(articleData.getName()) != null) {
            throw new IllegalArgumentException("Article with the same name already exists.");
        }

        foundArticle.setName(articleData.getName());
        foundArticle.setBrand(articleData.getBrand());
        foundArticle.setStock(articleData.getStock());
        foundArticle.setPurchasePrice(articleData.getPurchasePrice());
        foundArticle.setSalePrice(articleData.getSalePrice());
        foundArticle.setWeight(articleData.getWeight());
        foundArticle.setProviderId(articleData.getProviderId());
        foundArticle.setCategoryId(articleData.getCategoryId());

        articleRepository.updateArticle(
                foundArticle.getArticleId(),
                foundArticle.getName(),
                foundArticle.getBrand(),
                foundArticle.getStock(),
                foundArticle.getPurchasePrice(),
                foundArticle.getSalePrice(),
                foundArticle.getWeight(),
                foundArticle.getProviderId(),
                foundArticle.getCategoryId()
        );

        return foundArticle;
    }

    private void triggerLowStockNotification(Article article) {
        Notification notification = new Notification();
        notification.setMessage("Item '" + article.getName() + "' (ID: " + article.getArticleId() + ") is low on stock.");
        notification.setArticleId(article.getArticleId());
        notification.setRead(false);
        notificationRepository.save(notification);
    }
}
