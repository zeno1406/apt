package FACTORY;

import DTO.RoleDTO;
import INTERFACE.Builder;
import java.math.BigDecimal;

public class RoleBuilder implements Builder<RoleDTO> {
    private int id;
    private String name;
    private String description;
    private BigDecimal salaryCoefficient;

    public RoleBuilder id(int id) {
        this.id = id;
        return this;
    }

    public RoleBuilder name(String name) {
        this.name = name;
        return this;
    }

    public RoleBuilder description(String description) {
        this.description = description;
        return this;
    }

    public RoleBuilder salaryCoefficient(BigDecimal salaryCoefficient) {
        this.salaryCoefficient = salaryCoefficient;
        return this;
    }

    @Override
    public RoleDTO build() {
        return new RoleDTO(id, name, description, salaryCoefficient);
    }
}
