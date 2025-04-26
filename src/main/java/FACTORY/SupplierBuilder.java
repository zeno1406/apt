package FACTORY;

import DTO.SupplierDTO;
import INTERFACE.Builder;

public class SupplierBuilder implements Builder<SupplierDTO> {
    private int id;
    private boolean status;
    private String name;
    private String phone;
    private String address;

    public SupplierBuilder id(int id) {
        this.id = id;
        return this;
    }

    public SupplierBuilder status(boolean status) {
        this.status = status;
        return this;
    }

    public SupplierBuilder name(String name) {
        this.name = name;
        return this;
    }

    public SupplierBuilder phone(String phone) {
        this.phone = phone;
        return this;
    }

    public SupplierBuilder address(String address) {
        this.address = address;
        return this;
    }

    @Override
    public SupplierDTO build() {
        return new SupplierDTO(id, name, phone, address, status);
    }
}
