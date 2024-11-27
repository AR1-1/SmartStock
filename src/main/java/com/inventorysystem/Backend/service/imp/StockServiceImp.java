package com.inventorysystem.Backend.service.imp;

import com.inventorysystem.Backend.model.Stock;
import com.inventorysystem.Backend.repository.StockRepository;
import com.inventorysystem.Backend.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockServiceImp implements StockService {

    @Autowired
    private StockRepository stockRepository;

    @Override
    public boolean sellStock(Long articleId, int quantityToSell) {
        // Retrieve stock for the article sorted by FIFO (oldest first)
        List<Stock> stockList = stockRepository.findStocksForArticle(articleId);

        int quantitySold = 0;

        for (Stock stock : stockList) {
            if (stock.getStockQuantity() > 0 && quantitySold < quantityToSell) {
                int availableQuantity = stock.getStockQuantity();
                int quantityToReduce = Math.min(availableQuantity, quantityToSell - quantitySold);

                // Reduce the stock quantity
                stock.setStockQuantity(availableQuantity - quantityToReduce);
                stockRepository.save(stock);  // Save the updated stock

                quantitySold += quantityToReduce;

                if (quantitySold == quantityToSell) {
                    break;
                }
            }
        }

        // Return true if all quantity was sold, otherwise return false
        return quantitySold >= quantityToSell;
    }

    @Override
    public List<Stock> getStockForArticle(Long articleId) {
        // Return the stock list in FIFO order based on entry date
        return stockRepository.findStocksForArticle(articleId);
    }
}
