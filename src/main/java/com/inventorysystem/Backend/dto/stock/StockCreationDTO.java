package com.inventorysystem.Backend.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCreationDTO {
    private Long articleId;
    private Timestamp entryDate;
    private int quantity;
    private String batchId;
    private String status;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private double weight;
}
