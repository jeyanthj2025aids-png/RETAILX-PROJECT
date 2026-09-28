package com.retailx.inventory.dto;

import com.retailx.inventory.entity.MovementType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class StockMovementDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private MovementType movementType;
    private Integer quantity;
    private LocalDate movementDate;
    private LocalDateTime createdAt;
    private Integer currentStockAfter;

    public StockMovementDTO() {
    }

    public StockMovementDTO(Long id, Long productId, MovementType movementType, Integer quantity, LocalDate movementDate) {
        this.id = id;
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movementDate = movementDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductSku() {
        return productSku;
    }

    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDate getMovementDate() {
        return movementDate;
    }

    public void setMovementDate(LocalDate movementDate) {
        this.movementDate = movementDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getCurrentStockAfter() {
        return currentStockAfter;
    }

    public void setCurrentStockAfter(Integer currentStockAfter) {
        this.currentStockAfter = currentStockAfter;
    }
}
