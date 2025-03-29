package DTO;

import java.math.BigDecimal;

public class ProductDTO {
    private String id;
    private String name;
    private int stockQuantity;
    private BigDecimal sellingPrice;
    private boolean status;

    public ProductDTO() {
    }

    public ProductDTO(String id, String name, int stockQuantity, BigDecimal sellingPrice, boolean status) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.sellingPrice = sellingPrice;
        this.status = status;
    }

    public ProductDTO(ProductDTO product) {
        this.id = product.id;
        this.name = product.name;
        this.stockQuantity = product.stockQuantity;
        this.sellingPrice = product.sellingPrice;
        this.status = product.status;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
