package com.inventorysystem.Backend.controller;

import com.inventorysystem.Backend.model.Stock;
import com.inventorysystem.Backend.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/stock")
@CrossOrigin  // Allow only the frontend at localhost:3000 to access the API
public class StockController {

    private static final Logger logger = LoggerFactory.getLogger(StockController.class);

    @Autowired
    private StockService stockService;

    // Endpoint to sell stock for a specific article
    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestParam Long articleId, @RequestParam int quantityToSell) {
        try {
            // Log the stock selling request
            logger.info("Attempting to sell stock: articleId = {}, quantity = {}", articleId, quantityToSell);

            // Call the service to sell stock
            boolean success = stockService.sellStock(articleId, quantityToSell);
            if (success) {
                logger.info("Stock sold successfully: articleId = {}, quantity = {}", articleId, quantityToSell);
                return ResponseEntity.ok("Stock sold successfully.");
            } else {
                logger.warn("Insufficient stock: articleId = {}, requested quantity = {}", articleId, quantityToSell);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Not enough stock available.");
            }
        } catch (Exception e) {
            // Log the error
            logger.error("Error occurred while selling stock for articleId: {}: {}", articleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while processing the request.");
        }
    }

    // Endpoint to get the stock of a specific article, ordered by FIFO
    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<Stock>> getStockForArticle(@PathVariable Long articleId) {
        try {
            // Log the stock fetching request
            logger.info("Fetching stock for articleId: {}", articleId);

            List<Stock> stockList = stockService.getStockForArticle(articleId);  // Fetch stocks from service in FIFO order
            if (stockList.isEmpty()) {
                logger.warn("No stock found for articleId: {}", articleId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);  // If no stock is found, return NOT_FOUND
            }
            logger.info("Fetched stock for articleId: {}, stock count: {}", articleId, stockList.size());
            return ResponseEntity.ok(stockList);  // Return the stock list in FIFO order
        } catch (Exception e) {
            // Log the error
            logger.error("Error occurred while fetching stock for articleId: {}: {}", articleId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Additional methods like handling stock updates or queries can go here
}
