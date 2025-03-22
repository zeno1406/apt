package DTO;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@ToString
public class StatisticDTO {
    private LocalDateTime saveDate;
    private int invoiceId;
    private BigDecimal totalCapital;

    // Constructor mặc định
    public StatisticDTO() {
    }

    // Constructor đầy đủ
    public StatisticDTO(LocalDateTime saveDate, int invoiceId, BigDecimal totalCapital) {
        this.saveDate = saveDate;
        this.invoiceId = invoiceId;
        this.totalCapital = totalCapital;
    }

    // Constructor clone
    public StatisticDTO(StatisticDTO other) {
        if (other != null) {
            this.saveDate = other.saveDate;
            this.invoiceId = other.invoiceId;
            this.totalCapital = other.totalCapital;
        }
    }

    // Getters và Setters
    public LocalDateTime getSaveDate() {
        return saveDate;
    }

    public void setSaveDate(LocalDateTime saveDate) {
        this.saveDate = saveDate;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public BigDecimal getTotalCapital() {
        return totalCapital;
    }

    public void setTotalCapital(BigDecimal totalCapital) {
        this.totalCapital = totalCapital;
    }
}
