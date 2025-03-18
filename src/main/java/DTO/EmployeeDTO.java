package DTO;

import java.math.BigDecimal;
import java.util.Date;

public class EmployeeDTO extends PersonDTO {
    private String firstName;
    private String lastName;
    private BigDecimal salary;
    private int roleId;

    public EmployeeDTO(int id, String firstName, String lastName, BigDecimal salary, String imageUrl, Date dateOfBirth, int roleId, int status) {
        super(id, imageUrl, dateOfBirth, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.roleId = roleId;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
}

