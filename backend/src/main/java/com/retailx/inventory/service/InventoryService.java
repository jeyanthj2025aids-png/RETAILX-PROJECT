package com.retailx.inventory.service;

import com.retailx.inventory.dto.ProductStockResponseDTO;
import com.retailx.inventory.entity.Product;
import com.retailx.inventory.exception.ResourceNotFoundException;
import com.retailx.inventory.repository.ProductRepository;
import com.retailx.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;

    public InventoryService(StockMovementRepository stockMovementRepository, ProductRepository productRepository) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
    }

    /**
     * Calculates and returns current stock dynamically from movement history:
     * CURRENT_STOCK = SUM(PURCHASE + RETURN) - SUM(SALE + DAMAGE)
     */
    @Transactional(readOnly = true)
    public Integer getCurrentStock(Long productId) {
        Integer stock = stockMovementRepository.calculateStockByProductId(productId);
        return stock != null ? stock : 0;
    }

    @Transactional(readOnly = true)
    public ProductStockResponseDTO getProductStockDetails(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));

        int currentStock = getCurrentStock(productId);
        String status;
        if (currentStock == 0) {
            status = "OUT_OF_STOCK";
        } else if (currentStock <= product.getReorderThreshold()) {
            status = "LOW_STOCK";
        } else {
            status = "IN_STOCK";
        }

        return new ProductStockResponseDTO(
                product.getId(),
                currentStock,
                product.getReorderThreshold(),
                product.getReorderQuantity(),
                status
        );
    }
}
