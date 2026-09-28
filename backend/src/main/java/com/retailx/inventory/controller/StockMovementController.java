package com.retailx.inventory.controller;

import com.retailx.inventory.dto.CreateStockMovementRequest;
import com.retailx.inventory.dto.CreateStockMovementResponse;
import com.retailx.inventory.dto.StockMovementDTO;
import com.retailx.inventory.service.StockMovementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/stock-movements", "/api/movements"})
@Tag(name = "Stock Movements", description = "Endpoints for recording inventory stock movements and triggering automated reorder alerts")
@CrossOrigin(origins = "*")
public class StockMovementController {

    private final StockMovementService stockMovementService;

    public StockMovementController(StockMovementService stockMovementService) {
        this.stockMovementService = stockMovementService;
    }

    @PostMapping
    @Operation(summary = "Record a stock movement", description = "Logs a movement (PURCHASE, SALE, RETURN, DAMAGE), updates dynamic stock, and evaluates reorder threshold alerts")
    public ResponseEntity<CreateStockMovementResponse> recordMovement(@Valid @RequestBody CreateStockMovementRequest request) {
        CreateStockMovementResponse response = stockMovementService.recordMovement(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "List all stock movements", description = "Retrieves paginated audit trail of all inventory movements")
    public ResponseEntity<List<StockMovementDTO>> getAllMovements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<StockMovementDTO> movements = stockMovementService.getAllMovements(pageable);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Get movements for specific product", description = "Retrieves paginated stock movement audit trail for a single product")
    public ResponseEntity<List<StockMovementDTO>> getMovementsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<StockMovementDTO> movements = stockMovementService.getMovementsByProduct(productId, pageable);
        return ResponseEntity.ok(movements);
    }
}
