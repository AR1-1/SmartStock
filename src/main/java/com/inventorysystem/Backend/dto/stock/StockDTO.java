package com.inventorysystem.Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDTO {
    private Long id;
    private Long articleId;         // To identify the associated article
    private String articleName;     // Optional, for easier client use
    private Timestamp entryDate;
    private int quantity;
    private int stockQuantity;
    private String batchId;
    private String status;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private double weight;
}
