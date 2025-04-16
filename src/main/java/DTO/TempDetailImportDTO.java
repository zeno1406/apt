package DTO;

import java.math.BigDecimal;

public class TempDetailImportDTO {
    private int importId;
    private String productId;
    private String name;
    private int quantity;
    private BigDecimal price;
    private BigDecimal sellingPrice;
    private BigDecimal totalPrice;

    public TempDetailImportDTO() {
    }

    public TempDetailImportDTO(int importId, String productId, String name, int quantity,
                               BigDecimal price, BigDecimal sellingPrice, BigDecimal totalPrice) {
        this.importId = importId;
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.sellingPrice = sellingPrice;
        this.totalPrice = totalPrice;
    }

    public TempDetailImportDTO(TempDetailImportDTO other) {
        this.importId = other.importId;
        this.productId = other.productId;
        this.name = other.name;
        this.quantity = other.quantity;
        this.price = other.price;
        this.sellingPrice = other.sellingPrice;
        this.totalPrice = other.totalPrice;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
