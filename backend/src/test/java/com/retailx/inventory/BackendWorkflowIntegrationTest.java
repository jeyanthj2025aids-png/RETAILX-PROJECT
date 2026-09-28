package com.retailx.inventory;

import com.retailx.inventory.dto.*;
import com.retailx.inventory.entity.AlertStatus;
import com.retailx.inventory.entity.MovementType;
import com.retailx.inventory.exception.DuplicateSKUException;
import com.retailx.inventory.exception.InsufficientStockException;
import com.retailx.inventory.exception.ResourceNotFoundException;
import com.retailx.inventory.exception.ValidationException;
import com.retailx.inventory.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BackendWorkflowIntegrationTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private StockMovementService stockMovementService;

    @Autowired
    private ReorderAlertService reorderAlertService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private DashboardService dashboardService;

    @Test
    @DisplayName("Workflow 1: Create Product and verify in catalog")
    void testWorkflow1_CreateProduct() {
        ProductDTO created = productService.createProduct(
                new ProductDTO(null, "Coca-Cola 500ml", "COKE500-WF", 20, 100)
        );
        assertNotNull(created.getId());
        assertEquals("COKE500-WF", created.getSku());
        assertEquals(20, created.getReorderThreshold());
        assertEquals(100, created.getReorderQuantity());

        ProductDTO fetched = productService.getProductById(created.getId());
        assertEquals("Coca-Cola 500ml", fetched.getName());
    }

    @Test
    @DisplayName("Workflow 2: Record Movements (PURCHASE 150, SALE 20, SALE 15, DAMAGE 3 -> Stock 112)")
    void testWorkflow2_RecordMovements() {
        ProductDTO prod = productService.createProduct(
                new ProductDTO(null, "Coke Batch", "COKE-BATCH", 20, 100)
        );

        // 1. PURCHASE 150 -> Stock 150
        CreateStockMovementResponse r1 = stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, 150, LocalDate.of(2026, 9, 1))
        );
        assertEquals(150, r1.getCurrentStock());

        // 2. SALE 20 -> Stock 130
        CreateStockMovementResponse r2 = stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 20, LocalDate.of(2026, 9, 2))
        );
        assertEquals(130, r2.getCurrentStock());

        // 3. SALE 15 -> Stock 115
        CreateStockMovementResponse r3 = stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 15, LocalDate.of(2026, 9, 3))
        );
        assertEquals(115, r3.getCurrentStock());

        // 4. DAMAGE 3 -> Stock 112
        CreateStockMovementResponse r4 = stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.DAMAGE, 3, LocalDate.of(2026, 9, 4))
        );
        assertEquals(112, r4.getCurrentStock());
        assertEquals(112, inventoryService.getCurrentStock(prod.getId()));
    }

    @Test
    @DisplayName("Workflow 3: Trigger Reorder Alert when Stock drops to 17 (<= Threshold 20)")
    void testWorkflow3_TriggerReorderAlert() {
        ProductDTO prod = productService.createProduct(
                new ProductDTO(null, "Coke Alert Item", "COKE-ALERT", 20, 100)
        );

        stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, 112, LocalDate.of(2026, 9, 1))
        );

        // SALE 95 -> Stock = 17 (<= 20) -> Trigger Alert
        CreateStockMovementResponse saleRes = stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 95, LocalDate.of(2026, 9, 2))
        );
        assertEquals(17, saleRes.getCurrentStock());
        assertTrue(saleRes.isReorderAlertCreated());
        assertNotNull(saleRes.getAlertId());

        List<ReorderAlertDTO> openAlerts = reorderAlertService.getOpenAlerts();
        boolean alertFound = openAlerts.stream().anyMatch(a -> a.getProductId().equals(prod.getId()));
        assertTrue(alertFound);
    }

    @Test
    @DisplayName("Workflow 4: Duplicate Alert Prevention when Stock drops further (17 -> 16)")
    void testWorkflow4_DuplicateAlertPrevention() {
        ProductDTO prod = productService.createProduct(
                new ProductDTO(null, "Coke Dup Item", "COKE-DUP", 20, 100)
        );

        stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, 112, LocalDate.of(2026, 9, 1))
        );

        // SALE 95 -> Stock = 17 -> Alert #1 created
        stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 95, LocalDate.of(2026, 9, 2))
        );

        // SALE 1 -> Stock = 16 -> Alert should NOT be created again
        CreateStockMovementResponse sale2 = stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 1, LocalDate.of(2026, 9, 3))
        );
        assertEquals(16, sale2.getCurrentStock());
        assertFalse(sale2.isReorderAlertCreated(), "Should not create duplicate OPEN alert");

        long openCount = reorderAlertService.getOpenAlerts().stream()
                .filter(a -> a.getProductId().equals(prod.getId())).count();
        assertEquals(1, openCount);
    }

    @Test
    @DisplayName("Workflow 5: Fulfill Alert (Changes to FULFILLED, sets fulfilledAt, preserves history)")
    void testWorkflow5_FulfillAlert() {
        ProductDTO prod = productService.createProduct(
                new ProductDTO(null, "Fulfill Item", "FULFILL-01", 10, 50)
        );

        stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, 20, LocalDate.of(2026, 9, 1))
        );

        CreateStockMovementResponse res = stockMovementService.recordMovement(
                new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 15, LocalDate.of(2026, 9, 2))
        );
        Long alertId = res.getAlertId();
        assertNotNull(alertId);

        ReorderAlertDTO fulfilled = reorderAlertService.fulfillAlert(alertId);
        assertEquals(AlertStatus.FULFILLED, fulfilled.getStatus());
        assertNotNull(fulfilled.getFulfilledAt());

        // Cannot fulfill an already fulfilled alert
        assertThrows(ValidationException.class, () -> reorderAlertService.fulfillAlert(alertId));
    }

    @Test
    @DisplayName("Workflow 6: Create New Alert after Fulfillment when Stock Restored and Drops Again")
    void testWorkflow6_RealertAfterFulfill() {
        ProductDTO prod = productService.createProduct(
                new ProductDTO(null, "Realert Item", "REALERT-01", 20, 100)
        );

        // 1. Initial 112 -> Sale 96 -> Stock = 16 (<= 20) -> Alert #1 created
        stockMovementService.recordMovement(new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, 112, LocalDate.of(2026, 9, 1)));
        CreateStockMovementResponse res1 = stockMovementService.recordMovement(new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 96, LocalDate.of(2026, 9, 2)));
        assertTrue(res1.isReorderAlertCreated());

        // 2. Fulfill Alert #1
        reorderAlertService.fulfillAlert(res1.getAlertId());

        // 3. PURCHASE 100 -> Stock = 116 (no new alert)
        CreateStockMovementResponse resPurch = stockMovementService.recordMovement(new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, 100, LocalDate.of(2026, 9, 3)));
        assertEquals(116, resPurch.getCurrentStock());
        assertFalse(resPurch.isReorderAlertCreated());

        // 4. SALE 100 -> Stock drops to 16 (<= 20) -> New Alert #2 (OPEN) created!
        CreateStockMovementResponse res2 = stockMovementService.recordMovement(new CreateStockMovementRequest(prod.getId(), MovementType.SALE, 100, LocalDate.of(2026, 9, 4)));
        assertEquals(16, res2.getCurrentStock());
        assertTrue(res2.isReorderAlertCreated(), "New alert must be generated after prior fulfillment");
    }

    @Test
    @DisplayName("Workflow 7: Fast-Moving Report ranking only counts SALE movements")
    void testWorkflow7_FastMovingReport() {
        ProductDTO prodA = productService.createProduct(new ProductDTO(null, "Product Alpha", "ALPHA-01", 10, 50));
        ProductDTO prodB = productService.createProduct(new ProductDTO(null, "Product Beta", "BETA-01", 10, 50));

        // Inflow
        stockMovementService.recordMovement(new CreateStockMovementRequest(prodA.getId(), MovementType.PURCHASE, 200, LocalDate.of(2026, 9, 1)));
        stockMovementService.recordMovement(new CreateStockMovementRequest(prodB.getId(), MovementType.PURCHASE, 200, LocalDate.of(2026, 9, 1)));

        // Prod A: 50 SALEs
        stockMovementService.recordMovement(new CreateStockMovementRequest(prodA.getId(), MovementType.SALE, 50, LocalDate.of(2026, 9, 5)));

        // Prod B: 30 SALEs + 50 DAMAGE (DAMAGE should NOT count as sale)
        stockMovementService.recordMovement(new CreateStockMovementRequest(prodB.getId(), MovementType.SALE, 30, LocalDate.of(2026, 9, 6)));
        stockMovementService.recordMovement(new CreateStockMovementRequest(prodB.getId(), MovementType.DAMAGE, 50, LocalDate.of(2026, 9, 7)));

        List<FastMovingProductDTO> report = reportService.getFastMovingProducts(LocalDate.of(2026, 9, 1), LocalDate.of(2026, 9, 30));
        assertFalse(report.isEmpty());

        FastMovingProductDTO rank1 = report.stream().filter(p -> p.getProductId().equals(prodA.getId())).findFirst().orElseThrow();
        FastMovingProductDTO rank2 = report.stream().filter(p -> p.getProductId().equals(prodB.getId())).findFirst().orElseThrow();

        assertEquals(50, rank1.getUnitsSold());
        assertEquals(30, rank2.getUnitsSold());
        assertTrue(rank1.getRank() < rank2.getRank());
    }

    @Test
    @DisplayName("Workflow 8: Dashboard summary live database aggregation")
    void testWorkflow8_DashboardSummary() {
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        assertNotNull(summary);
        assertTrue(summary.getTotalProducts() >= 5);
        assertTrue(summary.getTotalStockUnits() >= 0);
        assertNotNull(summary.getRecentMovements());
        assertNotNull(summary.getOpenAlerts());
        assertNotNull(summary.getTopFastMovingProducts());
    }

    @Test
    @DisplayName("Edge Case: Reject negative and zero quantities in stock movements")
    void testEdgeCase_InvalidMovementQuantities() {
        ProductDTO prod = productService.createProduct(new ProductDTO(null, "Edge Item", "EDGE-QTY", 5, 20));

        assertThrows(ValidationException.class, () -> {
            stockMovementService.recordMovement(new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, 0, LocalDate.now()));
        });

        assertThrows(ValidationException.class, () -> {
            stockMovementService.recordMovement(new CreateStockMovementRequest(prod.getId(), MovementType.PURCHASE, -5, LocalDate.now()));
        });
    }

    @Test
    @DisplayName("Edge Case: Reject product updates with duplicate SKU across different products")
    void testEdgeCase_DuplicateSkuOnUpdate() {
        ProductDTO p1 = productService.createProduct(new ProductDTO(null, "Product One", "SKU-ONE", 10, 50));
        ProductDTO p2 = productService.createProduct(new ProductDTO(null, "Product Two", "SKU-TWO", 10, 50));

        // Attempting to rename p2 to SKU-ONE should fail with DuplicateSKUException
        assertThrows(DuplicateSKUException.class, () -> {
            productService.updateProduct(p2.getId(), new ProductDTO(p2.getId(), "Product Two New", "SKU-ONE", 10, 50));
        });
    }

    @Test
    @DisplayName("Edge Case: ResourceNotFoundException on invalid IDs")
    void testEdgeCase_ResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(999999L));
        assertThrows(ResourceNotFoundException.class, () -> reorderAlertService.getAlertById(999999L));
        assertThrows(ResourceNotFoundException.class, () -> stockMovementService.recordMovement(
                new CreateStockMovementRequest(999999L, MovementType.PURCHASE, 10, LocalDate.now())
        ));
    }
}
