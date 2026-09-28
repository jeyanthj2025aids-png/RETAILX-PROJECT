package com.retailx.inventory.service;

import com.retailx.inventory.dto.FastMovingProductDTO;
import com.retailx.inventory.exception.ValidationException;
import com.retailx.inventory.repository.StockMovementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {

    private final StockMovementRepository stockMovementRepository;

    public ReportService(StockMovementRepository stockMovementRepository) {
        this.stockMovementRepository = stockMovementRepository;
    }

    /**
     * Identifies fast-moving products by total SALE volume in the date range.
     * Only counts SALE movements; excludes PURCHASE, RETURN, DAMAGE.
     */
    @Transactional(readOnly = true)
    public List<FastMovingProductDTO> getFastMovingProducts(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new ValidationException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date must be before end date");
        }

        List<Object[]> rawResults = stockMovementRepository.findFastMovingProducts(startDate, endDate);
        List<FastMovingProductDTO> fastMovingList = new ArrayList<>();

        int currentRank = 1;
        for (Object[] row : rawResults) {
            Long productId = ((Number) row[0]).longValue();
            String productName = (String) row[1];
            String sku = (String) row[2];
            Long unitsSold = ((Number) row[3]).longValue();

            fastMovingList.add(new FastMovingProductDTO(
                    currentRank++,
                    productId,
                    productName,
                    sku,
                    unitsSold
            ));
        }

        return fastMovingList;
    }
}
