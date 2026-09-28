package com.retailx.inventory.dto;

import com.retailx.inventory.entity.AlertStatus;
import java.time.LocalDateTime;

public class ReorderAlertDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private Integer currentStockAtAlert;
    private Integer reorderThresholdAtAlert;
    private Integer reorderQuantity;
    private Integer currentStockNow;
    private AlertStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime fulfilledAt;

    public ReorderAlertDTO() {
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

    public Integer getCurrentStockAtAlert() {
        return currentStockAtAlert;
    }

    public void setCurrentStockAtAlert(Integer currentStockAtAlert) {
        this.currentStockAtAlert = currentStockAtAlert;
    }

    public Integer getReorderThresholdAtAlert() {
        return reorderThresholdAtAlert;
    }

    public void setReorderThresholdAtAlert(Integer reorderThresholdAtAlert) {
        this.reorderThresholdAtAlert = reorderThresholdAtAlert;
    }

    public Integer getReorderQuantity() {
        return reorderQuantity;
    }

    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }

    public Integer getCurrentStockNow() {
        return currentStockNow;
    }

    public void setCurrentStockNow(Integer currentStockNow) {
        this.currentStockNow = currentStockNow;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getFulfilledAt() {
        return fulfilledAt;
    }

    public void setFulfilledAt(LocalDateTime fulfilledAt) {
        this.fulfilledAt = fulfilledAt;
    }
}
