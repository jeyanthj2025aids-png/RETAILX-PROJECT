package com.retailx.inventory.service;

import com.retailx.inventory.dto.CreateStockMovementRequest;
import com.retailx.inventory.dto.CreateStockMovementResponse;
import com.retailx.inventory.dto.StockMovementDTO;
import com.retailx.inventory.entity.MovementType;
import com.retailx.inventory.entity.Product;
import com.retailx.inventory.entity.ReorderAlert;
import com.retailx.inventory.entity.StockMovement;
import com.retailx.inventory.exception.InsufficientStockException;
import com.retailx.inventory.exception.ResourceNotFoundException;
import com.retailx.inventory.exception.ValidationException;
import com.retailx.inventory.repository.ProductRepository;
import com.retailx.inventory.repository.StockMovementRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockMovementService {

    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final ReorderAlertService reorderAlertService;

    public StockMovementService(StockMovementRepository stockMovementRepository,
                                ProductRepository productRepository,
                                InventoryService inventoryService,
                                ReorderAlertService reorderAlertService) {
        this.stockMovementRepository = stockMovementRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.reorderAlertService = reorderAlertService;
    }

    /**
     * Records a stock movement, validates available stock, recalculates dynamic stock,
     * and evaluates whether a new OPEN reorder alert should be generated.
     */
    @Transactional
    public CreateStockMovementResponse recordMovement(CreateStockMovementRequest request) {
        if (request.getProductId() == null) {
            throw new ValidationException("Product ID must not be null");
        }
        if (request.getMovementType() == null) {
            throw new ValidationException("Movement type is required (PURCHASE, SALE, RETURN, DAMAGE)");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new ValidationException("Quantity must be greater than zero");
        }
        if (request.getMovementDate() == null) {
            throw new ValidationException("Movement date is required");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + request.getProductId() + " not found"));

        int currentStockBefore = inventoryService.getCurrentStock(product.getId());

        // For SALE & DAMAGE: validate sufficient stock
        if (request.getMovementType() == MovementType.SALE || request.getMovementType() == MovementType.DAMAGE) {
            if (request.getQuantity() > currentStockBefore) {
                throw new InsufficientStockException(
                        "Insufficient stock. Current stock: " + currentStockBefore + ", Requested: " + request.getQuantity()
                );
            }
        }

        StockMovement movement = new StockMovement(
                product,
                request.getMovementType(),
                request.getQuantity(),
                request.getMovementDate()
        );

        StockMovement savedMovement = stockMovementRepository.save(movement);

        // Dynamically recalculate stock after movement
        int updatedStock = inventoryService.getCurrentStock(product.getId());

        // Trigger alert check
        ReorderAlert createdAlert = reorderAlertService.checkAndCreateAlertIfNeeded(product.getId());

        boolean alertCreated = createdAlert != null;
        Long alertId = createdAlert != null ? createdAlert.getId() : null;

        StockMovementDTO movementDTO = mapToDTO(savedMovement, updatedStock);

        return new CreateStockMovementResponse(
                movementDTO,
                updatedStock,
                alertCreated,
                alertId
        );
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByProduct(Long productId, Pageable pageable) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product with id " + productId + " not found");
        }
        return stockMovementRepository.findByProductIdOrderByMovementDateDescCreatedAtDesc(productId, pageable).stream()
                .map(m -> mapToDTO(m, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product with id " + productId + " not found");
        }
        return stockMovementRepository.findByProductIdOrderByMovementDateDescCreatedAtDesc(productId).stream()
                .map(m -> mapToDTO(m, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getMovementsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Start date and end date are required");
        }
        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date must be before or equal to end date");
        }
        return stockMovementRepository.findByMovementDateBetweenOrderByMovementDateDescCreatedAtDesc(startDate, endDate).stream()
                .map(m -> mapToDTO(m, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getAllMovements(Pageable pageable) {
        return stockMovementRepository.findAllByOrderByMovementDateDescCreatedAtDesc(pageable).stream()
                .map(m -> mapToDTO(m, null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockMovementDTO> getAllMovements() {
        return stockMovementRepository.findAllByOrderByMovementDateDescCreatedAtDesc().stream()
                .map(m -> mapToDTO(m, null))
                .collect(Collectors.toList());
    }

    public StockMovementDTO mapToDTO(StockMovement movement, Integer currentStockAfter) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setProductId(movement.getProduct().getId());
        dto.setProductName(movement.getProduct().getName());
        dto.setProductSku(movement.getProduct().getSku());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setMovementDate(movement.getMovementDate());
        dto.setCreatedAt(movement.getCreatedAt());
        dto.setCurrentStockAfter(currentStockAfter != null ? currentStockAfter : inventoryService.getCurrentStock(movement.getProduct().getId()));
        return dto;
    }
}
