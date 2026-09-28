package com.retailx.inventory.repository;

import com.retailx.inventory.entity.MovementType;
import com.retailx.inventory.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {

    @Query("SELECT COALESCE(SUM(CASE " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.PURCHASE THEN sm.quantity " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.RETURN THEN sm.quantity " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.SALE THEN -sm.quantity " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.DAMAGE THEN -sm.quantity " +
           "ELSE 0 END), 0) " +
           "FROM StockMovement sm WHERE sm.product.id = :productId")
    Integer calculateStockByProductId(@Param("productId") Long productId);

    @Query("SELECT sm.product.id, COALESCE(SUM(CASE " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.PURCHASE THEN sm.quantity " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.RETURN THEN sm.quantity " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.SALE THEN -sm.quantity " +
           "WHEN sm.movementType = com.retailx.inventory.entity.MovementType.DAMAGE THEN -sm.quantity " +
           "ELSE 0 END), 0) " +
           "FROM StockMovement sm GROUP BY sm.product.id")
    List<Object[]> calculateStockForAllProducts();

    @Query("SELECT sm.product.id, sm.product.name, sm.product.sku, SUM(sm.quantity) as unitsSold " +
           "FROM StockMovement sm " +
           "WHERE sm.movementType = com.retailx.inventory.entity.MovementType.SALE " +
           "AND sm.movementDate >= :startDate AND sm.movementDate <= :endDate " +
           "GROUP BY sm.product.id, sm.product.name, sm.product.sku " +
           "ORDER BY unitsSold DESC")
    List<Object[]> findFastMovingProducts(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Page<StockMovement> findByProductIdOrderByMovementDateDescCreatedAtDesc(Long productId, Pageable pageable);

    List<StockMovement> findByProductIdOrderByMovementDateDescCreatedAtDesc(Long productId);

    Page<StockMovement> findAllByOrderByMovementDateDescCreatedAtDesc(Pageable pageable);

    List<StockMovement> findAllByOrderByMovementDateDescCreatedAtDesc();

    List<StockMovement> findTop10ByOrderByMovementDateDescCreatedAtDesc();

    List<StockMovement> findByMovementDateBetweenOrderByMovementDateDescCreatedAtDesc(LocalDate startDate, LocalDate endDate);
}
