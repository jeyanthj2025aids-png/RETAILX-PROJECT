package com.retailx.inventory.controller;

import com.retailx.inventory.dto.ReorderAlertDTO;
import com.retailx.inventory.entity.AlertStatus;
import com.retailx.inventory.service.ReorderAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/reorder-alerts", "/api/alerts"})
@Tag(name = "Reorder Alerts", description = "Endpoints for managing duplicate-protected reorder alerts and fulfillments")
@CrossOrigin(origins = "*")
public class ReorderAlertController {

    private final ReorderAlertService reorderAlertService;

    public ReorderAlertController(ReorderAlertService reorderAlertService) {
        this.reorderAlertService = reorderAlertService;
    }

    @GetMapping
    @Operation(summary = "List all reorder alerts", description = "Retrieves reorder alerts with optional status filtering (OPEN/FULFILLED) and pagination")
    public ResponseEntity<List<ReorderAlertDTO>> getAlerts(
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (status != null) {
            return ResponseEntity.ok(reorderAlertService.getAlertsByStatus(status, pageable));
        }
        return ResponseEntity.ok(reorderAlertService.getAllAlerts(pageable));
    }

    @GetMapping("/open")
    @Operation(summary = "List only OPEN alerts", description = "Retrieves active alerts requiring inventory replenishment")
    public ResponseEntity<List<ReorderAlertDTO>> getOpenAlerts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reorderAlertService.getOpenAlerts(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get single alert by ID", description = "Retrieves single alert record by its unique ID")
    public ResponseEntity<ReorderAlertDTO> getAlertById(@PathVariable Long id) {
        ReorderAlertDTO alert = reorderAlertService.getAlertById(id);
        return ResponseEntity.ok(alert);
    }

    @RequestMapping(value = "/{id}/fulfill", method = {RequestMethod.PUT, RequestMethod.POST})
    @Operation(summary = "Mark alert as FULFILLED", description = "Transitions an OPEN alert to FULFILLED, recording timestamp while preserving historical record")
    public ResponseEntity<ReorderAlertDTO> fulfillAlert(@PathVariable Long id) {
        ReorderAlertDTO fulfilled = reorderAlertService.fulfillAlert(id);
        return ResponseEntity.ok(fulfilled);
    }

    @PostMapping("/sync")
    @Operation(summary = "Sync alerts with low-stock inventory", description = "Scans all products and creates OPEN alerts for any low stock products missing an open alert")
    public ResponseEntity<String> syncAlerts() {
        reorderAlertService.syncAlertsForLowStock();
        return ResponseEntity.ok("Alerts synchronized with current stock levels");
    }
}
