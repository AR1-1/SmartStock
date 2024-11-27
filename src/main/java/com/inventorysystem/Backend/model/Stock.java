package com.inventorysystem.Backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many Stock entries can belong to one Article (e.g., multiple batches of pens)
    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    private Article article;

    // Date when the stock was entered into the system (typically when the stock was received)
    @Column(name = "entry_date", nullable = false)
    private Timestamp entryDate;

    // Date when this stock record was created in the system (automatically set when saved)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    // Last updated date, automatically updated when the record is modified
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    // Quantity of this stock entry
    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Stock count available (could be used if the stock is sold in parts or units)
    @Column(name = "stock_quantity", nullable = false)
    private int stockQuantity;

    // Batch ID for identifying the specific batch of stock
    @Column(name = "batch_id", nullable = false)
    private String batchId;

    // Status of the stock (e.g., 'available', 'sold', 'reserved')
    @Column(name = "status", nullable = false)
    private String status;

    // The price at which the stock was purchased
    @Column(name = "purchase_price")
    private BigDecimal purchasePrice;

    // The selling price of this stock (could vary for different batches)
    @Column(name = "sale_price")
    private BigDecimal salePrice;

    // The weight of this stock item (if applicable)
    @Column(name = "weight")
    private double weight;


}
