package com.retailx.inventory.controller;

import com.retailx.inventory.dto.DashboardSummaryDTO;
import com.retailx.inventory.dto.FastMovingProductDTO;
import com.retailx.inventory.service.DashboardService;
import com.retailx.inventory.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "Reports & Analytics", description = "Endpoints for fast-moving sales ranking and inventory analytics")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;
    private final DashboardService dashboardService;

    public ReportController(ReportService reportService, DashboardService dashboardService) {
        this.reportService = reportService;
        this.dashboardService = dashboardService;
    }

    @GetMapping({"/fast-moving-products", "/fast-moving"})
    @Operation(summary = "Generate fast-moving products report", description = "Aggregates total SALE volume per product within a date range and ranks them descending")
    public ResponseEntity<List<FastMovingProductDTO>> getFastMovingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<FastMovingProductDTO> fastMoving = reportService.getFastMovingProducts(startDate, endDate);
        return ResponseEntity.ok(fastMoving);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get system summary metrics", description = "Provides high-level metrics for dashboard statistics")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        DashboardSummaryDTO summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(summary);
    }
}
