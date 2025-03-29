package DTO;

import lombok.ToString;
import java.time.LocalDateTime;

@ToString
public abstract class BaseInformationDTO {
    protected int id;
    protected boolean status;
    protected LocalDateTime dateOfBirth;

    public BaseInformationDTO() {
    }

    public BaseInformationDTO(int id, LocalDateTime dateOfBirth, boolean status) {
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
    }

    public BaseInformationDTO(BaseInformationDTO other) {
        if (other != null) {
            this.id = other.id;
            this.status = other.status;
            this.dateOfBirth = other.dateOfBirth; // LocalDateTime là immutable, không cần tạo mới
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

    public LocalDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
