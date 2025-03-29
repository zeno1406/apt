package FACTORY;

import DTO.CustomerDTO;
import INTERFACE.Builder;
import java.time.LocalDateTime;

public class CustomerBuilder implements Builder<CustomerDTO> {
    private int id;
    private boolean status;
    private LocalDateTime dateOfBirth;
    private String firstName;
    private String lastName;
    private String phone;
    private String address;

    public CustomerBuilder id(int id) {
        this.id = id;
        return this;
    }

    public CustomerBuilder status(boolean status) {
        this.status = status;
        return this;
    }

    public CustomerBuilder dateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public CustomerBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public CustomerBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public CustomerBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public CustomerBuilder address(String address) {
        this.address = address;
        return this;
    }

    @Override
    public CustomerDTO build() {
        return new CustomerDTO(id, firstName, lastName, phone, address, dateOfBirth, status);
    }
}
