package DTO;

public class SupplierDTO {
    private int id;
    private boolean status;
    private String name;
    private String phone;
    private String address;

    // Constructor mặc định
    public SupplierDTO() {
    }

    // Constructor đầy đủ
    public SupplierDTO(int id, String name, String phone, String address, boolean status) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.status = status;
    }

    // Constructor sao chép (clone)
    public SupplierDTO(SupplierDTO other) {
        if (other != null) {
            this.id = other.id;
            this.status = other.status;
            this.name = other.name;
            this.phone = other.phone;
            this.address = other.address;
        }
    }

    // Getter và Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
