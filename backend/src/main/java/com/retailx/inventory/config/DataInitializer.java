package com.retailx.inventory.config;

import com.retailx.inventory.dto.CreateStockMovementRequest;
import com.retailx.inventory.entity.MovementType;
import com.retailx.inventory.entity.Product;
import com.retailx.inventory.repository.ProductRepository;
import com.retailx.inventory.service.StockMovementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductRepository productRepository;
    private final StockMovementService stockMovementService;

    public DataInitializer(ProductRepository productRepository,
                           StockMovementService stockMovementService) {
        this.productRepository = productRepository;
        this.stockMovementService = stockMovementService;
    }

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            log.info("Seeding initial sample products and stock movements...");

            // 1. Create Sample Products
            Product coke = productRepository.save(new Product("Coca-Cola 500ml", "COKE500", 20, 100));
            Product parleg = productRepository.save(new Product("Parle-G Biscuits", "PARLEG100", 15, 80));
            Product lux = productRepository.save(new Product("Lux Soap", "LUX125", 10, 50));
            Product shampoo = productRepository.save(new Product("Head & Shoulders Shampoo", "HS200", 8, 40));
            Product rice = productRepository.save(new Product("Aashirvaad Rice 5kg", "AARRICE5", 12, 60));

            // 2. Create Movements using StockMovementService to automatically apply rules and trigger alerts
            // Coca-Cola Movements
            stockMovementService.recordMovement(new CreateStockMovementRequest(coke.getId(), MovementType.PURCHASE, 150, LocalDate.of(2026, 9, 1)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(coke.getId(), MovementType.SALE, 20, LocalDate.of(2026, 9, 2)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(coke.getId(), MovementType.SALE, 15, LocalDate.of(2026, 9, 3)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(coke.getId(), MovementType.DAMAGE, 3, LocalDate.of(2026, 9, 4)));

            // Parle-G Movements (Final stock = 15, threshold = 15 -> triggers OPEN alert!)
            stockMovementService.recordMovement(new CreateStockMovementRequest(parleg.getId(), MovementType.PURCHASE, 100, LocalDate.of(2026, 9, 1)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(parleg.getId(), MovementType.SALE, 30, LocalDate.of(2026, 9, 5)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(parleg.getId(), MovementType.SALE, 25, LocalDate.of(2026, 9, 6)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(parleg.getId(), MovementType.SALE, 30, LocalDate.of(2026, 9, 7)));

            // Lux Soap Movements
            stockMovementService.recordMovement(new CreateStockMovementRequest(lux.getId(), MovementType.PURCHASE, 80, LocalDate.of(2026, 9, 1)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(lux.getId(), MovementType.SALE, 20, LocalDate.of(2026, 9, 8)));

            // Head & Shoulders Movements
            stockMovementService.recordMovement(new CreateStockMovementRequest(shampoo.getId(), MovementType.PURCHASE, 50, LocalDate.of(2026, 9, 1)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(shampoo.getId(), MovementType.SALE, 18, LocalDate.of(2026, 9, 9)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(shampoo.getId(), MovementType.SALE, 15, LocalDate.of(2026, 9, 10)));

            // Aashirvaad Rice Movements (Final stock = 5, threshold = 12 -> triggers OPEN alert!)
            stockMovementService.recordMovement(new CreateStockMovementRequest(rice.getId(), MovementType.PURCHASE, 120, LocalDate.of(2026, 9, 1)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(rice.getId(), MovementType.SALE, 50, LocalDate.of(2026, 9, 11)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(rice.getId(), MovementType.SALE, 40, LocalDate.of(2026, 9, 12)));
            stockMovementService.recordMovement(new CreateStockMovementRequest(rice.getId(), MovementType.SALE, 25, LocalDate.of(2026, 9, 13)));

            log.info("Sample products and movements seeded successfully!");
        }
    }
}
