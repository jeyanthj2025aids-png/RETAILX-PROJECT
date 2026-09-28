package com.retailx.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "reorder_alerts", indexes = {
    @Index(name = "idx_alert_product_id", columnList = "product_id"),
    @Index(name = "idx_alert_status", columnList = "status")
})
public class ReorderAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Product is required")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Product product;

    @NotNull
    @Column(name = "current_stock_at_alert", nullable = false)
    private Integer currentStockAtAlert;

    @NotNull
    @Column(name = "reorder_threshold_at_alert", nullable = false)
    private Integer reorderThresholdAtAlert;

    @NotNull
    @Column(name = "reorder_quantity", nullable = false)
    private Integer reorderQuantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertStatus status = AlertStatus.OPEN;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "fulfilled_at")
    private LocalDateTime fulfilledAt;

    public ReorderAlert() {
    }

    public ReorderAlert(Product product, Integer currentStockAtAlert, Integer reorderThresholdAtAlert, Integer reorderQuantity) {
        this.product = product;
        this.currentStockAtAlert = currentStockAtAlert;
        this.reorderThresholdAtAlert = reorderThresholdAtAlert;
        this.reorderQuantity = reorderQuantity;
        this.status = AlertStatus.OPEN;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
