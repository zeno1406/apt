package FACTORY;

import DTO.EmployeeDTO;
import INTERFACE.Builder;
import java.math.BigDecimal;
import java.util.Date;

public class EmployeeBuilder implements Builder<EmployeeDTO> {
    private int id;
    private boolean status;
    private String imageUrl;
    private Date dateOfBirth;
    private String firstName;
    private String lastName;
    private BigDecimal salary;
    private int roleId;

    public EmployeeBuilder id(int id) {
        this.id = id;
        return this;
    }

    public EmployeeBuilder status(boolean status) {
        this.status = status;
        return this;
    }

    public EmployeeBuilder imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public EmployeeBuilder dateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public EmployeeBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public EmployeeBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public EmployeeBuilder salary(BigDecimal salary) {
        this.salary = salary;
        return this;
    }

    public EmployeeBuilder roleId(int roleId) {
        this.roleId = roleId;
        return this;
    }

    @Override
    public EmployeeDTO build() {
        return new EmployeeDTO(id, firstName, lastName, salary, imageUrl, dateOfBirth, roleId, status);
    }
}
