package com.retailx.inventory.dto;

import java.util.List;

public class DashboardSummaryDTO {
    private long totalProducts;
    private long totalStockUnits;
    private long openReorderAlerts;
    private long productsLowStock;
    private List<StockMovementDTO> recentMovements;
    private List<ReorderAlertDTO> openAlerts;
    private List<FastMovingProductDTO> topFastMovingProducts;

    public DashboardSummaryDTO() {
    }

    public DashboardSummaryDTO(long totalProducts, long totalStockUnits, long openReorderAlerts, long productsLowStock,
                               List<StockMovementDTO> recentMovements, List<ReorderAlertDTO> openAlerts,
                               List<FastMovingProductDTO> topFastMovingProducts) {
        this.totalProducts = totalProducts;
        this.totalStockUnits = totalStockUnits;
        this.openReorderAlerts = openReorderAlerts;
        this.productsLowStock = productsLowStock;
        this.recentMovements = recentMovements;
        this.openAlerts = openAlerts;
        this.topFastMovingProducts = topFastMovingProducts;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalStockUnits() {
        return totalStockUnits;
    }

    public void setTotalStockUnits(long totalStockUnits) {
        this.totalStockUnits = totalStockUnits;
    }

    public long getOpenReorderAlerts() {
        return openReorderAlerts;
    }

    public void setOpenReorderAlerts(long openReorderAlerts) {
        this.openReorderAlerts = openReorderAlerts;
    }

    public long getProductsLowStock() {
        return productsLowStock;
    }

    public void setProductsLowStock(long productsLowStock) {
        this.productsLowStock = productsLowStock;
    }

    public List<StockMovementDTO> getRecentMovements() {
        return recentMovements;
    }

    public void setRecentMovements(List<StockMovementDTO> recentMovements) {
        this.recentMovements = recentMovements;
    }

    public List<ReorderAlertDTO> getOpenAlerts() {
        return openAlerts;
    }

    public void setOpenAlerts(List<ReorderAlertDTO> openAlerts) {
        this.openAlerts = openAlerts;
    }

    public List<FastMovingProductDTO> getTopFastMovingProducts() {
        return topFastMovingProducts;
    }

    public void setTopFastMovingProducts(List<FastMovingProductDTO> topFastMovingProducts) {
        this.topFastMovingProducts = topFastMovingProducts;
    }
}
