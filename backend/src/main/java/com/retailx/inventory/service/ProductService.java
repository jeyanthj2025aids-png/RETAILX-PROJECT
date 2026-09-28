package com.retailx.inventory.service;

import com.retailx.inventory.dto.ProductDTO;
import com.retailx.inventory.dto.ProductStockResponseDTO;
import com.retailx.inventory.entity.AlertStatus;
import com.retailx.inventory.entity.Product;
import com.retailx.inventory.exception.DuplicateSKUException;
import com.retailx.inventory.exception.ResourceNotFoundException;
import com.retailx.inventory.exception.ValidationException;
import com.retailx.inventory.repository.ProductRepository;
import com.retailx.inventory.repository.ReorderAlertRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final ReorderAlertRepository reorderAlertRepository;

    public ProductService(ProductRepository productRepository,
                          InventoryService inventoryService,
                          ReorderAlertRepository reorderAlertRepository) {
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
        this.reorderAlertRepository = reorderAlertRepository;
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            throw new ValidationException("SKU is required");
        }
        if (dto.getReorderThreshold() == null || dto.getReorderThreshold() < 0) {
            throw new ValidationException("Reorder threshold must be 0 or greater");
        }
        if (dto.getReorderQuantity() == null || dto.getReorderQuantity() <= 0) {
            throw new ValidationException("Reorder quantity must be greater than zero");
        }

        String normalizedSku = dto.getSku().trim().toUpperCase();
        if (productRepository.existsBySku(normalizedSku)) {
            throw new DuplicateSKUException("SKU '" + normalizedSku + "' already exists");
        }

        Product product = new Product(
                dto.getName().trim(),
                normalizedSku,
                dto.getReorderThreshold(),
                dto.getReorderQuantity()
        );

        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));

        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new ValidationException("Product name is required");
        }
        if (dto.getSku() == null || dto.getSku().trim().isEmpty()) {
            throw new ValidationException("SKU is required");
        }
        if (dto.getReorderThreshold() == null || dto.getReorderThreshold() < 0) {
            throw new ValidationException("Reorder threshold must be 0 or greater");
        }
        if (dto.getReorderQuantity() == null || dto.getReorderQuantity() <= 0) {
            throw new ValidationException("Reorder quantity must be greater than zero");
        }

        String normalizedSku = dto.getSku().trim().toUpperCase();
        if (productRepository.existsBySkuAndIdNot(normalizedSku, id)) {
            throw new DuplicateSKUException("SKU '" + normalizedSku + "' already exists");
        }

        product.setName(dto.getName().trim());
        product.setSku(normalizedSku);
        product.setReorderThreshold(dto.getReorderThreshold());
        product.setReorderQuantity(dto.getReorderQuantity());

        Product updated = productRepository.save(product);
        return mapToDTO(updated);
    }

    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return mapToDTO(product);
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id " + id + " not found");
        }
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ProductStockResponseDTO getProductStock(Long id) {
        return inventoryService.getProductStockDetails(id);
    }

    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    public ProductDTO mapToDTO(Product product) {
        int currentStock = inventoryService.getCurrentStock(product.getId());
        boolean hasOpenAlert = reorderAlertRepository.existsByProductIdAndStatus(product.getId(), AlertStatus.OPEN);

        String status;
        if (currentStock == 0) {
            status = "OUT_OF_STOCK";
        } else if (currentStock <= product.getReorderThreshold()) {
            status = "LOW_STOCK";
        } else {
            status = "IN_STOCK";
        }

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setSku(product.getSku());
        dto.setReorderThreshold(product.getReorderThreshold());
        dto.setReorderQuantity(product.getReorderQuantity());
        dto.setCurrentStock(currentStock);
        dto.setStatus(status);
        dto.setLowStock(currentStock <= product.getReorderThreshold());
        dto.setHasOpenAlert(hasOpenAlert);
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}
