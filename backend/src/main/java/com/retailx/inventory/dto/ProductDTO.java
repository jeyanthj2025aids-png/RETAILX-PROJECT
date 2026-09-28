package com.retailx.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class ProductDTO {
    private Long id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    @NotNull(message = "Reorder threshold is required")
    @Min(value = 0, message = "Reorder threshold must be 0 or greater")
    private Integer reorderThreshold = 0;

    @NotNull(message = "Reorder quantity is required")
    @Min(value = 1, message = "Reorder quantity must be greater than zero")
    private Integer reorderQuantity;

    private Integer currentStock;
    private String status; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK
    private boolean lowStock;
    private boolean hasOpenAlert;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductDTO() {
    }

    public ProductDTO(Long id, String name, String sku, Integer reorderThreshold, Integer reorderQuantity) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.reorderThreshold = reorderThreshold;
        this.reorderQuantity = reorderQuantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public Integer getReorderQuantity() {
        return reorderQuantity;
    }

    public void setReorderQuantity(Integer reorderQuantity) {
        this.reorderQuantity = reorderQuantity;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    public boolean isHasOpenAlert() {
        return hasOpenAlert;
    }

    public void setHasOpenAlert(boolean hasOpenAlert) {
        this.hasOpenAlert = hasOpenAlert;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
