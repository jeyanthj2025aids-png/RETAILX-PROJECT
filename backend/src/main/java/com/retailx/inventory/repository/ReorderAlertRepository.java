package com.retailx.inventory.repository;

import com.retailx.inventory.entity.AlertStatus;
import com.retailx.inventory.entity.ReorderAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReorderAlertRepository extends JpaRepository<ReorderAlert, Long> {

    boolean existsByProductIdAndStatus(Long productId, AlertStatus status);

    Optional<ReorderAlert> findByProductIdAndStatus(Long productId, AlertStatus status);

    Page<ReorderAlert> findByStatusOrderByCreatedAtDesc(AlertStatus status, Pageable pageable);

    List<ReorderAlert> findByStatusOrderByCreatedAtDesc(AlertStatus status);

    Page<ReorderAlert> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ReorderAlert> findAllByOrderByCreatedAtDesc();

    List<ReorderAlert> findByProductIdOrderByCreatedAtDesc(Long productId);

    long countByStatus(AlertStatus status);
}
