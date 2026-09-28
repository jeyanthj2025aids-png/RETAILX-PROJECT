package com.retailx.inventory;

import com.retailx.inventory.dto.*;
import com.retailx.inventory.entity.AlertStatus;
import com.retailx.inventory.entity.MovementType;
import com.retailx.inventory.exception.DuplicateSKUException;
import com.retailx.inventory.exception.InsufficientStockException;
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
public class InventoryServiceTest {

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
    @DisplayName("Should dynamically calculate current stock from PURCHASE, SALE, RETURN, DAMAGE")
    void testDynamicStockCalculation() {
        ProductDTO product = productService.createProduct(
                new ProductDTO(null, "Test Item A", "TEST_A", 10, 50)
        );

        assertEquals(0, inventoryService.getCurrentStock(product.getId()));

        // 1. PURCHASE 100 -> Stock = 100
        CreateStockMovementResponse res1 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.PURCHASE, 100, LocalDate.of(2026, 9, 1)
        ));
        assertEquals(100, res1.getCurrentStock());
        assertEquals(100, inventoryService.getCurrentStock(product.getId()));
        assertFalse(res1.isReorderAlertCreated());

        // 2. SALE 30 -> Stock = 70
        CreateStockMovementResponse res2 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.SALE, 30, LocalDate.of(2026, 9, 2)
        ));
        assertEquals(70, res2.getCurrentStock());
        assertEquals(70, inventoryService.getCurrentStock(product.getId()));

        // 3. RETURN 5 -> Stock = 75
        CreateStockMovementResponse res3 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.RETURN, 5, LocalDate.of(2026, 9, 3)
        ));
        assertEquals(75, res3.getCurrentStock());
        assertEquals(75, inventoryService.getCurrentStock(product.getId()));

        // 4. DAMAGE 10 -> Stock = 65
        CreateStockMovementResponse res4 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.DAMAGE, 10, LocalDate.of(2026, 9, 4)
        ));
        assertEquals(65, res4.getCurrentStock());
        assertEquals(65, inventoryService.getCurrentStock(product.getId()));
    }

    @Test
    @DisplayName("Should reject SALE when requested quantity exceeds available stock")
    void testInsufficientStockValidation() {
        ProductDTO product = productService.createProduct(
                new ProductDTO(null, "Test Item B", "TEST_B", 5, 20)
        );

        stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.PURCHASE, 15, LocalDate.of(2026, 9, 1)
        ));

        assertThrows(InsufficientStockException.class, () -> {
            stockMovementService.recordMovement(new CreateStockMovementRequest(
                    product.getId(), MovementType.SALE, 20, LocalDate.of(2026, 9, 2)
            ));
        });

        // Current stock should remain 15
        assertEquals(15, inventoryService.getCurrentStock(product.getId()));
    }

    @Test
    @DisplayName("Should prevent duplicate OPEN alerts for the same product")
    void testPreventDuplicateOpenAlerts() {
        // Threshold = 20
        ProductDTO product = productService.createProduct(
                new ProductDTO(null, "Test Item C", "TEST_C", 20, 50)
        );

        // Initial Purchase 50
        stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.PURCHASE, 50, LocalDate.of(2026, 9, 1)
        ));

        // SALE 30 -> Stock becomes 20 (equals threshold) -> Triggers Alert #1 (OPEN)
        CreateStockMovementResponse res1 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.SALE, 30, LocalDate.of(2026, 9, 2)
        ));
        assertTrue(res1.isReorderAlertCreated());
        assertNotNull(res1.getAlertId());

        List<ReorderAlertDTO> openAlerts1 = reorderAlertService.getOpenAlerts();
        long alertsForProduct = openAlerts1.stream().filter(a -> a.getProductId().equals(product.getId())).count();
        assertEquals(1, alertsForProduct, "Exactly one OPEN alert should exist");

        // SALE 5 -> Stock drops further to 15 -> Should NOT create Alert #2
        CreateStockMovementResponse res2 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.SALE, 5, LocalDate.of(2026, 9, 3)
        ));
        assertFalse(res2.isReorderAlertCreated(), "Should not create duplicate alert");

        List<ReorderAlertDTO> openAlerts2 = reorderAlertService.getOpenAlerts();
        long alertsForProductAfter = openAlerts2.stream().filter(a -> a.getProductId().equals(product.getId())).count();
        assertEquals(1, alertsForProductAfter, "Still exactly one OPEN alert should exist (no duplicate)");
    }

    @Test
    @DisplayName("Should allow new alert after existing alert is fulfilled and stock drops again")
    void testFulfillAndRealertWorkflow() {
        ProductDTO product = productService.createProduct(
                new ProductDTO(null, "Test Item D", "TEST_D", 10, 40)
        );

        stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.PURCHASE, 20, LocalDate.of(2026, 9, 1)
        ));

        // SALE 12 -> Stock = 8 (<= threshold 10) -> Alert #1 created (OPEN)
        CreateStockMovementResponse res1 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.SALE, 12, LocalDate.of(2026, 9, 2)
        ));
        assertTrue(res1.isReorderAlertCreated());

        List<ReorderAlertDTO> alerts = reorderAlertService.getAlertsByProductId(product.getId());
        assertEquals(1, alerts.size());
        ReorderAlertDTO openAlert = alerts.get(0);
        assertEquals(AlertStatus.OPEN, openAlert.getStatus());

        // Admin fulfills Alert #1
        ReorderAlertDTO fulfilled = reorderAlertService.fulfillAlert(openAlert.getId());
        assertEquals(AlertStatus.FULFILLED, fulfilled.getStatus());
        assertNotNull(fulfilled.getFulfilledAt());

        // Stock restored above threshold: PURCHASE 50 -> Stock = 58
        stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.PURCHASE, 50, LocalDate.of(2026, 9, 3)
        ));

        // Stock drops below threshold again: SALE 50 -> Stock = 8 (<= 10)
        CreateStockMovementResponse res2 = stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.SALE, 50, LocalDate.of(2026, 9, 4)
        ));
        assertTrue(res2.isReorderAlertCreated(), "New alert should be created when stock drops again");

        // Now there should be 1 FULFILLED alert and 1 new OPEN alert
        List<ReorderAlertDTO> allProductAlerts = reorderAlertService.getAlertsByProductId(product.getId());
        assertEquals(2, allProductAlerts.size());

        long openCount = allProductAlerts.stream().filter(a -> a.getStatus() == AlertStatus.OPEN).count();
        long fulfilledCount = allProductAlerts.stream().filter(a -> a.getStatus() == AlertStatus.FULFILLED).count();
        assertEquals(1, openCount);
        assertEquals(1, fulfilledCount);
    }

    @Test
    @DisplayName("Should query fast-moving products by date range and reject invalid date range")
    void testFastMovingProductsReport() {
        LocalDate start = LocalDate.of(2026, 9, 1);
        LocalDate end = LocalDate.of(2026, 9, 15);

        List<FastMovingProductDTO> fastMoving = reportService.getFastMovingProducts(start, end);
        assertNotNull(fastMoving);
        assertFalse(fastMoving.isEmpty());

        // Verify sorted descending by unitsSold
        for (int i = 0; i < fastMoving.size() - 1; i++) {
            assertTrue(fastMoving.get(i).getUnitsSold() >= fastMoving.get(i + 1).getUnitsSold());
            assertEquals(i + 1, fastMoving.get(i).getRank());
        }

        // Test invalid date range: startDate > endDate
        assertThrows(ValidationException.class, () -> {
            reportService.getFastMovingProducts(LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 10));
        });
    }

    @Test
    @DisplayName("Should reject creation of product with duplicate SKU")
    void testDuplicateSkuRejection() {
        productService.createProduct(new ProductDTO(null, "Unique Item", "UNIQUE1", 5, 20));

        assertThrows(DuplicateSKUException.class, () -> {
            productService.createProduct(new ProductDTO(null, "Another Item", "UNIQUE1", 10, 30));
        });
    }

    @Test
    @DisplayName("Should return accurate dashboard summary metrics")
    void testDashboardSummary() {
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        assertNotNull(summary);
        assertTrue(summary.getTotalProducts() >= 5);
        assertTrue(summary.getTotalStockUnits() > 0);
        assertNotNull(summary.getRecentMovements());
        assertNotNull(summary.getOpenAlerts());
        assertNotNull(summary.getTopFastMovingProducts());
    }

    @Test
    @DisplayName("Should report correct product stock details and status")
    void testProductStockDetails() {
        ProductDTO product = productService.createProduct(
                new ProductDTO(null, "Stock Status Item", "STOCK_STAT", 10, 50)
        );

        ProductStockResponseDTO stock0 = productService.getProductStock(product.getId());
        assertEquals("OUT_OF_STOCK", stock0.getStatus());
        assertEquals(0, stock0.getCurrentStock());

        // Add 8 units (<= threshold 10)
        stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.PURCHASE, 8, LocalDate.of(2026, 9, 1)
        ));

        ProductStockResponseDTO stockLow = productService.getProductStock(product.getId());
        assertEquals("LOW_STOCK", stockLow.getStatus());
        assertEquals(8, stockLow.getCurrentStock());

        // Add 20 more units (28 > 10)
        stockMovementService.recordMovement(new CreateStockMovementRequest(
                product.getId(), MovementType.PURCHASE, 20, LocalDate.of(2026, 9, 2)
        ));

        ProductStockResponseDTO stockIn = productService.getProductStock(product.getId());
        assertEquals("IN_STOCK", stockIn.getStatus());
        assertEquals(28, stockIn.getCurrentStock());
    }
}
