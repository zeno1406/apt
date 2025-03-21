package DTO;

import java.util.Date;

public class CustomerDTO extends PersonDTO {
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    public CustomerDTO(int id, String firstName, String lastName, String phone, String address, String imageUrl, Date dateOfBirth, boolean status) {
        super(id, imageUrl, dateOfBirth, status);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.address = address;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
