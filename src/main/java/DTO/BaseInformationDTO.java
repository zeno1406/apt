package DTO;

import lombok.ToString;

import java.time.LocalDateTime;

@ToString
public abstract class BaseInformationDTO {
    protected int id;
    protected boolean status;
    protected String imageUrl;
    protected LocalDateTime dateOfBirth;

    public BaseInformationDTO() {
    }

    public BaseInformationDTO(int id, String imageUrl, LocalDateTime dateOfBirth, boolean status) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }

    public BaseInformationDTO(BaseInformationDTO other) {
        if (other != null) {
            this.id = other.id;
            this.status = other.status;
            this.imageUrl = other.imageUrl;
            this.dateOfBirth = other.dateOfBirth; // LocalDate là immutable, không cần tạo mới
        }
    }

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
