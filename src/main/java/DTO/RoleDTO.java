package DTO;

import java.math.BigDecimal;

public class RoleDTO {
    private int id;
    private String name;
    private String description;
    private BigDecimal salaryCoefficient;

    public RoleDTO() {
    }

    public RoleDTO(int id, String name, String description, BigDecimal salaryCoefficient) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.salaryCoefficient = salaryCoefficient;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getSalaryCoefficient() {
        return salaryCoefficient;
    }

    public void setSalaryCoefficient(BigDecimal salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
    }
}
