package com.retailx.inventory.dto;

public class FastMovingProductDTO {
    private Integer rank;
    private Long productId;
    private String name;
    private String sku;
    private Long unitsSold;

    public FastMovingProductDTO() {
    }

    public FastMovingProductDTO(Integer rank, Long productId, String name, String sku, Long unitsSold) {
        this.rank = rank;
        this.productId = productId;
        this.name = name;
        this.sku = sku;
        this.unitsSold = unitsSold;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(Long unitsSold) {
        this.unitsSold = unitsSold;
    }
}
