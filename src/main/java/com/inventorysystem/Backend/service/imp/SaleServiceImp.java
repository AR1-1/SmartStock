package com.inventorysystem.Backend.service.imp;

import com.inventorysystem.Backend.dto.sale.*;
import com.inventorysystem.Backend.mapper.SaleMapper;
import com.inventorysystem.Backend.model.Article;
import com.inventorysystem.Backend.model.Sale;
import com.inventorysystem.Backend.model.SaleDetail;
import com.inventorysystem.Backend.repository.ArticleRepository;
import com.inventorysystem.Backend.repository.SaleDetailRepository;
import com.inventorysystem.Backend.repository.SaleRepository;
import com.inventorysystem.Backend.service.SaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SaleServiceImp implements SaleService {

    private static final Logger logger = LoggerFactory.getLogger(SaleServiceImp.class);

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private SaleDetailRepository saleDetailRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private SaleMapper saleMapper;

    @Override
    @Transactional
    public SaleDetailDTO createSale(SaleCreationDTO sale) {
        if (sale.getArticles().isEmpty()) {
            logger.error("Sale is empty");
            throw new IllegalArgumentException("Sale cannot be empty");
        }

        List<SaleCreationArticleDTO> validArticles = new ArrayList<>();
        Long totalSalePrice = 0L;

        // Validate articles and calculate total price
        for (SaleCreationArticleDTO article : sale.getArticles()) {
            Article foundArticle = articleRepository.getArticleById(article.getArticleId());
            if (foundArticle == null || article.getArticleQuantity() < 1) {
                logger.warn("Invalid article id or quantity: {}", article);
                continue;
            }

            validArticles.add(article);
            totalSalePrice += foundArticle.getSalePrice() * article.getArticleQuantity();
        }

        if (validArticles.isEmpty()) {
            logger.error("No valid articles found in sale");
            throw new IllegalArgumentException("No valid articles found in sale");
        }

        // Create the Sale object
        Sale saleEntity = new Sale();
        saleEntity.setCustomerId(sale.getCustomerId());
        saleEntity.setUserId(sale.getSessionUserId());
        saleEntity.setTotalValue(totalSalePrice.intValue());

        Sale savedSale = saleRepository.save(saleEntity);
        Long newSaleId = savedSale.getSaleId();

        // Create sale details and update article stock
        for (SaleCreationArticleDTO article : validArticles) {
            Article foundArticle = articleRepository.getArticleById(article.getArticleId());
            Long totalValue = (long) (foundArticle.getSalePrice() * article.getArticleQuantity());

            saleDetailRepository.createSaleDetail(
                    newSaleId,
                    foundArticle.getArticleId(),
                    article.getArticleQuantity(),
                    totalValue
            );

            // Update article stock
            int newStock = foundArticle.getStock() - article.getArticleQuantity();
            foundArticle.setStock(Math.max(newStock, 0)); // Ensure stock is not negative
            articleRepository.save(foundArticle);
        }

        // Return sale details
        return getSaleById(newSaleId);
    }

    @Override
    @Transactional
    public SalesPageDTO getAllSales(String criteria, Integer page, Integer pageSize) {
        SalesPageDTO pagedSalesResponse = new SalesPageDTO();

        Page<Sale> salePage;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by("saleId").descending());

        if (criteria == null || criteria.isEmpty()) {
            salePage = saleRepository.findAll(pageable);
        } else {
            salePage = saleRepository.findAllSales(criteria, pageable); // Assuming you have a custom query for sales
        }

        List<SaleDTO> sales = salePage.getContent().stream()
                .map(sale -> saleMapper.saleToDTO(sale))
                .collect(Collectors.toList());

        pagedSalesResponse.setPage(salePage.getNumber() + 1);
        pagedSalesResponse.setPageSize(salePage.getSize());
        pagedSalesResponse.setTotalRecords(salePage.getTotalElements());
        pagedSalesResponse.setTotalPages(salePage.getTotalPages() > 0 ? salePage.getTotalPages() : 1);
        pagedSalesResponse.setSales(sales);

        return pagedSalesResponse;
    }

    @Override
    @Transactional
    public SaleDetailDTO getSaleById(Long id) {
        Sale foundSale = saleRepository.getSaleById(id);
        if (foundSale == null) {
            logger.error("Sale not found with id: {}", id);
            throw new IllegalArgumentException("Sale not found");
        }

        List<SaleDetail> foundSaleDetails = saleDetailRepository.getAllSaleDetails(id);
        return saleMapper.saleDetailToDTO(foundSale, foundSaleDetails);
    }
}
