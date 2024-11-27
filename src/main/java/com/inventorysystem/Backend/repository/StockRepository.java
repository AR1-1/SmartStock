package com.inventorysystem.Backend.repository;

import com.inventorysystem.Backend.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {


    @Query("SELECT s FROM Stock s WHERE s.article.articleId = :articleId ORDER BY s.entryDate ASC")
    List<Stock> findStocksForArticle(@Param("articleId") Long articleId);

}
