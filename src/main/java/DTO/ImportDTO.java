package DTO;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
public class ImportDTO {
    private int id;
    private LocalDateTime createDate;
    private int employeeId;
    private int supplierId;
    private BigDecimal profitPercent;
    private BigDecimal totalPrice;

    public ImportDTO() {
    }

    public ImportDTO(int id, LocalDateTime createDate, int employeeId, int supplierId, BigDecimal profitPercent, BigDecimal totalPrice) {
        this.id = id;
        this.createDate = createDate;
        this.employeeId = employeeId;
        this.supplierId = supplierId;
        this.profitPercent = profitPercent;
        this.totalPrice = totalPrice;
    }

    public ImportDTO(ImportDTO other) {
        if (other != null) {
            this.id = other.id;
            this.createDate = other.createDate;
            this.employeeId = other.employeeId;
            this.supplierId = other.supplierId;
            this.profitPercent = other.profitPercent;
            this.totalPrice = other.totalPrice;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
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
