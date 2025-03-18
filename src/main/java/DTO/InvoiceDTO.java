package DTO;

import java.math.BigDecimal;
import java.util.Date;

public class InvoiceDTO {
    private int id;
    private Date createDate;
    private int employeeId;
    private int customerId;
    private String discountCode;
    private BigDecimal totalPrice;

    public InvoiceDTO() {
    }

    public InvoiceDTO(int id, Date createDate, int employeeId, int customerId, String discountCode, BigDecimal totalPrice) {
        this.id = id;
        this.createDate = createDate;
        this.employeeId = employeeId;
        this.customerId = customerId;
        this.discountCode = discountCode;
        this.totalPrice = totalPrice;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
