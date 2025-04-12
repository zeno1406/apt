package DTO;

import java.math.BigDecimal;

public class DetailDiscountDTO {
    private String discountCode;
    private BigDecimal totalPriceInvoice;
    private BigDecimal discountAmount;

    // Constructors
    public DetailDiscountDTO() {
    }

    public DetailDiscountDTO(String discountCode, BigDecimal totalPriceInvoice, BigDecimal discountAmount) {
        this.discountCode = discountCode;
        this.totalPriceInvoice = totalPriceInvoice;
        this.discountAmount = discountAmount;
    }

    public DetailDiscountDTO(DetailDiscountDTO detailDiscountDTO) {
        this.discountCode = detailDiscountDTO.discountCode;
        this.totalPriceInvoice = detailDiscountDTO.totalPriceInvoice;
        this.discountAmount = detailDiscountDTO.discountAmount;
    }

    // Getters and Setters
    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public BigDecimal getTotalPriceInvoice() {
        return totalPriceInvoice;
    }

    public void setTotalPriceInvoice(BigDecimal totalPriceInvoice) {
        this.totalPriceInvoice = totalPriceInvoice;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }
}
