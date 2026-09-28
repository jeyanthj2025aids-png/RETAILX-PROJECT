package com.retailx.inventory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.retailx.inventory.dto.CreateStockMovementRequest;
import com.retailx.inventory.dto.ProductDTO;
import com.retailx.inventory.entity.MovementType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BackendApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Product REST API: Create, Get, Update, Delete, and Stock endpoints")
    void testProductRestEndpoints() throws Exception {
        // 1. Create Product
        ProductDTO newProduct = new ProductDTO(null, "Organic Green Tea", "TEA-001", 15, 60);
        String createResponse = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Organic Green Tea"))
                .andExpect(jsonPath("$.sku").value("TEA-001"))
                .andExpect(jsonPath("$.reorderThreshold").value(15))
                .andExpect(jsonPath("$.reorderQuantity").value(60))
                .andReturn().getResponse().getContentAsString();

        ProductDTO created = objectMapper.readValue(createResponse, ProductDTO.class);
        Long productId = created.getId();

        // 2. Reject duplicate SKU (409 Conflict)
        ProductDTO duplicateProduct = new ProductDTO(null, "Another Green Tea", "TEA-001", 10, 50);
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateProduct)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.path").value("/api/products"));

        // 3. Get Product by ID
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.sku").value("TEA-001"));

        // 4. Update Product
        ProductDTO updatePayload = new ProductDTO(productId, "Premium Organic Green Tea", "TEA-001-PREM", 20, 80);
        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Premium Organic Green Tea"))
                .andExpect(jsonPath("$.sku").value("TEA-001-PREM"))
                .andExpect(jsonPath("$.reorderThreshold").value(20));

        // 5. Get Product Stock
        mockMvc.perform(get("/api/products/{id}/stock", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(productId))
                .andExpect(jsonPath("$.currentStock").value(0))
                .andExpect(jsonPath("$.status").value("OUT_OF_STOCK"));

        // 6. Delete Product
        mockMvc.perform(delete("/api/products/{id}", productId))
                .andExpect(status().isNoContent());

        // 7. Verify 404 after deletion
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Stock Movement REST API: Record movement, Alert triggering, and Insufficient Stock check")
    void testStockMovementRestEndpoints() throws Exception {
        // Create Product with threshold 10
        ProductDTO product = new ProductDTO(null, "Almond Milk", "MILK-ALM", 10, 40);
        String prodRes = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long productId = objectMapper.readValue(prodRes, ProductDTO.class).getId();

        // 1. PURCHASE 30 units -> Stock = 30
        CreateStockMovementRequest purchaseReq = new CreateStockMovementRequest(
                productId, MovementType.PURCHASE, 30, LocalDate.of(2026, 9, 1)
        );
        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentStock").value(30))
                .andExpect(jsonPath("$.reorderAlertCreated").value(false));

        // 2. Reject SALE when insufficient stock (Attempt SALE of 50 units when stock is 30) -> 400 Bad Request
        CreateStockMovementRequest overSaleReq = new CreateStockMovementRequest(
                productId, MovementType.SALE, 50, LocalDate.of(2026, 9, 2)
        );
        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(overSaleReq)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Insufficient Stock"))
                .andExpect(jsonPath("$.message", containsString("Insufficient stock")));

        // 3. SALE 22 units -> Stock becomes 8 (<= threshold 10) -> Reorder alert created!
        CreateStockMovementRequest saleReq = new CreateStockMovementRequest(
                productId, MovementType.SALE, 22, LocalDate.of(2026, 9, 3)
        );
        mockMvc.perform(post("/api/stock-movements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saleReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.currentStock").value(8))
                .andExpect(jsonPath("$.reorderAlertCreated").value(true))
                .andExpect(jsonPath("$.alertId").isNumber());

        // 4. List all movements
        mockMvc.perform(get("/api/stock-movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));

        // 5. List movements by product
        mockMvc.perform(get("/api/stock-movements/product/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Reorder Alert REST API: List alerts, Open alerts, Fulfill alert")
    void testReorderAlertRestEndpoints() throws Exception {
        // Create product and trigger alert
        ProductDTO product = new ProductDTO(null, "Dark Roast Coffee", "COFFEE-DRK", 15, 50);
        String prodRes = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        Long productId = objectMapper.readValue(prodRes, ProductDTO.class).getId();

        // Initial Purchase 20
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateStockMovementRequest(
                        productId, MovementType.PURCHASE, 20, LocalDate.of(2026, 9, 1)
                )))).andExpect(status().isCreated());

        // Sale 10 -> Stock = 10 (<= 15) -> Trigger alert
        mockMvc.perform(post("/api/stock-movements")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateStockMovementRequest(
                        productId, MovementType.SALE, 10, LocalDate.of(2026, 9, 2)
                )))).andExpect(status().isCreated());

        // 1. Get Open Alerts
        String openAlertsRes = mockMvc.perform(get("/api/reorder-alerts/open"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andReturn().getResponse().getContentAsString();

        com.retailx.inventory.dto.ReorderAlertDTO[] alerts = objectMapper.readValue(openAlertsRes, com.retailx.inventory.dto.ReorderAlertDTO[].class);
        Long alertId = null;
        for (com.retailx.inventory.dto.ReorderAlertDTO a : alerts) {
            if (a.getProductId().equals(productId)) {
                alertId = a.getId();
                break;
            }
        }
        assertNotNull(alertId, "Alert ID for product should not be null");

        // 2. Get single alert by ID
        mockMvc.perform(get("/api/reorder-alerts/{id}", alertId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(alertId))
                .andExpect(jsonPath("$.status").value("OPEN"));

        // 3. Mark alert as FULFILLED (PUT /api/reorder-alerts/{id}/fulfill)
        mockMvc.perform(put("/api/reorder-alerts/{id}/fulfill", alertId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FULFILLED"))
                .andExpect(jsonPath("$.fulfilledAt").isNotEmpty());

        // 4. Attempting to fulfill again should fail (400 Bad Request)
        mockMvc.perform(put("/api/reorder-alerts/{id}/fulfill", alertId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Dashboard and Reports REST API")
    void testDashboardAndReportsEndpoints() throws Exception {
        // 1. Dashboard summary
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").isNumber())
                .andExpect(jsonPath("$.totalStockUnits").isNumber())
                .andExpect(jsonPath("$.openReorderAlerts").isNumber())
                .andExpect(jsonPath("$.productsLowStock").isNumber())
                .andExpect(jsonPath("$.recentMovements").isArray())
                .andExpect(jsonPath("$.openAlerts").isArray())
                .andExpect(jsonPath("$.topFastMovingProducts").isArray());

        // 2. Fast-moving report with valid date range
        mockMvc.perform(get("/api/reports/fast-moving-products")
                        .param("startDate", "2026-09-01")
                        .param("endDate", "2026-09-30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // 3. Fast-moving report with invalid date range (start > end) -> 400 Bad Request
        mockMvc.perform(get("/api/reports/fast-moving-products")
                        .param("startDate", "2026-09-30")
                        .param("endDate", "2026-09-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message", containsString("Start date must be before end date")));
    }
}
