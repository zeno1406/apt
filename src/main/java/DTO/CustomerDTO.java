package DTO;

import java.time.LocalDateTime;

public class CustomerDTO extends BaseInformationDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    public CustomerDTO(int id, String firstName, String lastName, String phone, String address, LocalDateTime dateOfBirth, boolean status) {
        super(id, dateOfBirth, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }

    public CustomerDTO(CustomerDTO other) {
        super(other); // Gọi constructor clone của PersonDTO nếu có
        this.firstName = other.firstName;
        this.lastName = other.lastName;
        this.phone = other.phone;
        this.address = other.address;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }
}
