package DTO;

import lombok.ToString;

import java.math.BigDecimal;
@ToString
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

    public RoleDTO(RoleDTO other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.salaryCoefficient = other.salaryCoefficient;
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
