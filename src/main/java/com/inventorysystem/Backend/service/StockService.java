package com.inventorysystem.Backend.service;

import com.inventorysystem.Backend.model.Stock;

import java.util.List;

public interface StockService {
    boolean sellStock(Long articleId, int quantityToSell);  // Method to sell stock
    List<Stock> getStockForArticle(Long articleId);
}
