package DTO;

import java.math.BigDecimal;

public class StatisticQuarterDTO {
    private int employeeId;
    private BigDecimal q1;
    private BigDecimal q2;
    private BigDecimal q3;
    private BigDecimal q4;
    private BigDecimal total;

    public StatisticQuarterDTO(int employeeId, BigDecimal q1, BigDecimal q2, BigDecimal q3, BigDecimal q4, BigDecimal total) {
        this.employeeId = employeeId;
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.total = total;
    }

    // Getter và Setter

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public BigDecimal getQ1() {
        return q1;
    }

    public void setQ1(BigDecimal q1) {
        this.q1 = q1;
    }

    public BigDecimal getQ2() {
        return q2;
    }

    public void setQ2(BigDecimal q2) {
        this.q2 = q2;
    }

    public BigDecimal getQ3() {
        return q3;
    }

    public void setQ3(BigDecimal q3) {
        this.q3 = q3;
    }

    public BigDecimal getQ4() {
        return q4;
    }

    public void setQ4(BigDecimal q4) {
        this.q4 = q4;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
