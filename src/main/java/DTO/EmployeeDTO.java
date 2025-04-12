package DTO;

import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@ToString(callSuper = true)
public class EmployeeDTO extends BaseInformationDTO {
    private String firstName;
    private String lastName;
    private BigDecimal salary;
    private int roleId;

    public EmployeeDTO() {

    }

    public EmployeeDTO(int id, String firstName, String lastName, BigDecimal salary, LocalDateTime dateOfBirth, int roleId, boolean status) {
        super(id, dateOfBirth, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.salary = salary;
        this.roleId = roleId;
    }

    public EmployeeDTO(EmployeeDTO other) {
        super(other); // Gọi constructor clone của PersonDTO nếu có
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.salary = other.salary != null ? new BigDecimal(other.salary.toString()) : null;
        this.roleId = other.roleId;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}

