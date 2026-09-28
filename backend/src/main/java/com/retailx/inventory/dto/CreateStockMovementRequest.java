package com.retailx.inventory.dto;

import com.retailx.inventory.entity.MovementType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class CreateStockMovementRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Movement type is required (PURCHASE, SALE, RETURN, DAMAGE)")
    private MovementType movementType;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private Integer quantity;

    @NotNull(message = "Movement date is required")
    private LocalDate movementDate;

    public CreateStockMovementRequest() {
    }

    public CreateStockMovementRequest(Long productId, MovementType movementType, Integer quantity, LocalDate movementDate) {
        this.productId = productId;
        this.movementType = movementType;
        this.quantity = quantity;
        this.movementDate = movementDate;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
