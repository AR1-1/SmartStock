package com.inventorysystem.Backend.mapper;


import com.inventorysystem.Backend.dto.StockDTO;
import com.inventorysystem.Backend.dto.stock.StockCreationDTO;
import com.inventorysystem.Backend.model.Article;
import com.inventorysystem.Backend.model.Stock;
import org.springframework.stereotype.Component;

@Component
public class StockMapper {

    // Map Stock entity to StockDTO
    public StockDTO toDTO(Stock stock) {
        if (stock == null) {
            return null;
        }

        StockDTO dto = new StockDTO();
        dto.setId(stock.getId());
        dto.setArticleId(stock.getArticle().getArticleId());
        dto.setArticleName(stock.getArticle().getName()); // Assuming Article has a 'name' field
        dto.setEntryDate(stock.getEntryDate());
        dto.setQuantity(stock.getQuantity());
        dto.setStockQuantity(stock.getStockQuantity());
        dto.setBatchId(stock.getBatchId());
        dto.setStatus(stock.getStatus());
        dto.setPurchasePrice(stock.getPurchasePrice());
        dto.setSalePrice(stock.getSalePrice());
        dto.setWeight(stock.getWeight());
        return dto;
    }

    // Map StockCreationDTO to Stock entity
    public Stock toEntity(StockCreationDTO dto) {
        if (dto == null) {
            return null;
        }

        Stock stock = new Stock();
        stock.setArticle(new Article(dto.getArticleId())); // Assuming Article has a constructor for ID
        stock.setEntryDate(dto.getEntryDate());
        stock.setQuantity(dto.getQuantity());
        stock.setStockQuantity(dto.getQuantity()); // Initial stock_quantity matches quantity
        stock.setBatchId(dto.getBatchId());
        stock.setStatus(dto.getStatus());
        stock.setPurchasePrice(dto.getPurchasePrice());
        stock.setSalePrice(dto.getSalePrice());
        stock.setWeight(dto.getWeight());
        return stock;
    }
}
