package DTO;

import java.math.BigDecimal;

public class DetailImportDTO {
    private int importId;
    private String productId;
    private int quantity;
    private BigDecimal price;
    private BigDecimal profitPercent;
    private BigDecimal totalPrice;

    public DetailImportDTO() {
    }

    public DetailImportDTO(int importId, String productId, int quantity, BigDecimal price, BigDecimal profitPercent, BigDecimal totalPrice) {
        this.importId = importId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.profitPercent = profitPercent;
        this.totalPrice = totalPrice;
    }

    public int getImportId() {
        return importId;
    }

    public void setImportId(int importId) {
        this.importId = importId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getProfitPercent() {
        return profitPercent;
    }

    public void setProfitPercent(BigDecimal profitPercent) {
        this.profitPercent = profitPercent;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
