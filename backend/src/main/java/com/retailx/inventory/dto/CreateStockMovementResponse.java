package com.retailx.inventory.dto;

public class CreateStockMovementResponse {
    private StockMovementDTO movement;
    private Integer currentStock;
    private boolean reorderAlertCreated;
    private Long alertId;

    public CreateStockMovementResponse() {
    }

    public CreateStockMovementResponse(StockMovementDTO movement, Integer currentStock, boolean reorderAlertCreated, Long alertId) {
        this.movement = movement;
        this.currentStock = currentStock;
        this.reorderAlertCreated = reorderAlertCreated;
        this.alertId = alertId;
    }

    public StockMovementDTO getMovement() {
        return movement;
    }

    public void setMovement(StockMovementDTO movement) {
        this.movement = movement;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public boolean isReorderAlertCreated() {
        return reorderAlertCreated;
    }

    public void setReorderAlertCreated(boolean reorderAlertCreated) {
        this.reorderAlertCreated = reorderAlertCreated;
    }

    public Long getAlertId() {
        return alertId;
    }

    public void setAlertId(Long alertId) {
        this.alertId = alertId;
    }
}
