package com.retailx.inventory.service;

import com.retailx.inventory.dto.DashboardSummaryDTO;
import com.retailx.inventory.dto.FastMovingProductDTO;
import com.retailx.inventory.dto.ReorderAlertDTO;
import com.retailx.inventory.dto.StockMovementDTO;
import com.retailx.inventory.entity.AlertStatus;
import com.retailx.inventory.entity.Product;
import com.retailx.inventory.entity.StockMovement;
import com.retailx.inventory.repository.ProductRepository;
import com.retailx.inventory.repository.ReorderAlertRepository;
import com.retailx.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ReorderAlertRepository reorderAlertRepository;
    private final InventoryService inventoryService;
    private final StockMovementService stockMovementService;
    private final ReorderAlertService reorderAlertService;
    private final ReportService reportService;

    public DashboardService(ProductRepository productRepository,
                            StockMovementRepository stockMovementRepository,
                            ReorderAlertRepository reorderAlertRepository,
                            InventoryService inventoryService,
                            StockMovementService stockMovementService,
                            ReorderAlertService reorderAlertService,
                            ReportService reportService) {
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.reorderAlertRepository = reorderAlertRepository;
        this.inventoryService = inventoryService;
        this.stockMovementService = stockMovementService;
        this.reorderAlertService = reorderAlertService;
        this.reportService = reportService;
    }

    /**
     * Aggregates all dashboard summary metrics dynamically from database entities.
     * All values are calculated from live records (no hardcoded metrics).
     */
    @Transactional(readOnly = true)
    public DashboardSummaryDTO getDashboardSummary() {
        List<Product> products = productRepository.findAll();
        long totalProducts = products.size();

        long totalStockUnits = 0;
        long productsLowStock = 0;

        for (Product product : products) {
            int currentStock = inventoryService.getCurrentStock(product.getId());
            totalStockUnits += currentStock;
            if (currentStock <= product.getReorderThreshold()) {
                productsLowStock++;
            }
        }

        long openReorderAlerts = reorderAlertRepository.countByStatus(AlertStatus.OPEN);

        // Recent 10 movements
        List<StockMovement> topMovements = stockMovementRepository.findTop10ByOrderByMovementDateDescCreatedAtDesc();
        List<StockMovementDTO> recentMovements = topMovements.stream()
                .map(m -> stockMovementService.mapToDTO(m, null))
                .collect(Collectors.toList());

        // Open alerts
        List<ReorderAlertDTO> openAlerts = reorderAlertService.getOpenAlerts();

        // Top fast moving products in the last 30 days
        LocalDate now = LocalDate.now();
        LocalDate thirtyDaysAgo = now.minusDays(30);
        List<FastMovingProductDTO> topFastMovingProducts = reportService.getFastMovingProducts(thirtyDaysAgo, now);

        return new DashboardSummaryDTO(
                totalProducts,
                totalStockUnits,
                openReorderAlerts,
                productsLowStock,
                recentMovements,
                openAlerts,
                topFastMovingProducts
        );
    }
}
