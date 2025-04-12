package DTO;

import lombok.Builder;
import lombok.Data;

public class CategoryDTO {
    private int id;
    private String name;
    private boolean status;

    public CategoryDTO() {
    }

    public CategoryDTO(int id, String name, boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public CategoryDTO(CategoryDTO other) {
        if (other != null) { // Kiểm tra null để tránh NullPointerException
            this.id = other.id;
            this.name = other.name;
            this.status = other.status;
        }
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

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
