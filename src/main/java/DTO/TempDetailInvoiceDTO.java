package DTO;

import java.math.BigDecimal;

public class TempDetailInvoiceDTO {
    private int invoiceId;
    private String productId;
    private String name;
    private int quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;

    // Constructor không tham số
    public TempDetailInvoiceDTO() {
    }

    // Constructor đầy đủ tham số
    public TempDetailInvoiceDTO(int invoiceId, String productId, String name, int quantity, BigDecimal price, BigDecimal totalPrice) {
        this.invoiceId = invoiceId;
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    // Constructor clone
    public TempDetailInvoiceDTO(TempDetailInvoiceDTO other) {
        this.invoiceId = other.invoiceId;
        this.productId = other.productId;
        this.name = other.name;
        this.quantity = other.quantity;
        this.price = other.price;
        this.totalPrice = other.totalPrice;
    }

    // Getters và Setters
    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
