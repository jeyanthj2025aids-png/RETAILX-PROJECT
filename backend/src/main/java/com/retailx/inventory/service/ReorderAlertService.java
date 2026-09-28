package com.retailx.inventory.service;

import com.retailx.inventory.dto.ReorderAlertDTO;
import com.retailx.inventory.entity.AlertStatus;
import com.retailx.inventory.entity.Product;
import com.retailx.inventory.entity.ReorderAlert;
import com.retailx.inventory.exception.ResourceNotFoundException;
import com.retailx.inventory.exception.ValidationException;
import com.retailx.inventory.repository.ProductRepository;
import com.retailx.inventory.repository.ReorderAlertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReorderAlertService {

    private final ReorderAlertRepository reorderAlertRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public ReorderAlertService(ReorderAlertRepository reorderAlertRepository,
                               ProductRepository productRepository,
                               InventoryService inventoryService) {
        this.reorderAlertRepository = reorderAlertRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    /**
     * Checks if dynamic stock is at or below threshold, and creates an OPEN alert if none exists.
     * Prevents duplicate OPEN alerts for the same product.
     */
    @Transactional
    public ReorderAlert checkAndCreateAlertIfNeeded(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " not found"));

        int currentStock = inventoryService.getCurrentStock(productId);

        if (currentStock <= product.getReorderThreshold()) {
            boolean hasOpenAlert = reorderAlertRepository.existsByProductIdAndStatus(productId, AlertStatus.OPEN);
            if (!hasOpenAlert) {
                ReorderAlert newAlert = new ReorderAlert(
                        product,
                        currentStock,
                        product.getReorderThreshold(),
                        product.getReorderQuantity()
                );
                return reorderAlertRepository.save(newAlert);
            }
        }
        return null;
    }

    @Transactional
    public ReorderAlertDTO fulfillAlert(Long alertId) {
        ReorderAlert alert = reorderAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Reorder alert with id " + alertId + " not found"));

        if (alert.getStatus() != AlertStatus.OPEN) {
            throw new ValidationException("Alert with id " + alertId + " is not in OPEN status (current status: " + alert.getStatus() + ")");
        }

        alert.setStatus(AlertStatus.FULFILLED);
        alert.setFulfilledAt(LocalDateTime.now());
        ReorderAlert saved = reorderAlertRepository.save(alert);
        return mapToDTO(saved);
    }

    @Transactional(readOnly = true)
    public ReorderAlertDTO getAlertById(Long alertId) {
        ReorderAlert alert = reorderAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Reorder alert with id " + alertId + " not found"));
        return mapToDTO(alert);
    }

    @Transactional(readOnly = true)
    public List<ReorderAlertDTO> getAllAlerts(Pageable pageable) {
        return reorderAlertRepository.findAllByOrderByCreatedAtDesc(pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReorderAlertDTO> getAllAlerts() {
        return reorderAlertRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReorderAlertDTO> getAlertsByStatus(AlertStatus status, Pageable pageable) {
        return reorderAlertRepository.findByStatusOrderByCreatedAtDesc(status, pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReorderAlertDTO> getOpenAlerts(Pageable pageable) {
        return getAlertsByStatus(AlertStatus.OPEN, pageable);
    }

    @Transactional(readOnly = true)
    public List<ReorderAlertDTO> getOpenAlerts() {
        return reorderAlertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.OPEN).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReorderAlertDTO> getAlertsByProductId(Long productId) {
        return reorderAlertRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void syncAlertsForLowStock() {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            checkAndCreateAlertIfNeeded(product.getId());
        }
    }

    public ReorderAlertDTO mapToDTO(ReorderAlert alert) {
        ReorderAlertDTO dto = new ReorderAlertDTO();
        dto.setId(alert.getId());
        dto.setProductId(alert.getProduct().getId());
        dto.setProductName(alert.getProduct().getName());
        dto.setProductSku(alert.getProduct().getSku());
        dto.setCurrentStockAtAlert(alert.getCurrentStockAtAlert());
        dto.setReorderThresholdAtAlert(alert.getReorderThresholdAtAlert());
        dto.setReorderQuantity(alert.getReorderQuantity());
        dto.setCurrentStockNow(inventoryService.getCurrentStock(alert.getProduct().getId()));
        dto.setStatus(alert.getStatus());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setFulfilledAt(alert.getFulfilledAt());
        return dto;
    }
}
