package com.retailx.inventory.dto;

public class ProductStockResponseDTO {
    private Long productId;
    private Integer currentStock;
    private Integer reorderThreshold;
    private Integer reorderQuantity;
    private String status; // IN_STOCK, LOW_STOCK, OUT_OF_STOCK

    public ProductStockResponseDTO() {
    }

    public ProductStockResponseDTO(Long productId, Integer currentStock, Integer reorderThreshold, Integer reorderQuantity, String status) {
        this.productId = productId;
        this.currentStock = currentStock;
        this.reorderThreshold = reorderThreshold;
        this.reorderQuantity = reorderQuantity;
        this.status = status;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
