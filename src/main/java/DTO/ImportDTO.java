package DTO;

import java.math.BigDecimal;
import java.util.Date;

public class ImportDTO {
    private int id;
    private Date createDate;
    private int employeeId;
    private int supplierId;
    private BigDecimal totalPrice;

    public ImportDTO() {
    }

    public ImportDTO(int id, Date createDate, int employeeId, int supplierId, BigDecimal totalPrice) {
        this.id = id;
        this.createDate = createDate;
        this.employeeId = employeeId;
        this.supplierId = supplierId;
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

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
