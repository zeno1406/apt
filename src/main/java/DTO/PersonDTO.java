package DTO;

import java.util.Date;

public abstract class PersonDTO extends BaseDTO {
    protected String imageUrl;
    protected Date dateOfBirth;

    public PersonDTO() {
    }

    public PersonDTO(int id, String imageUrl, Date dateOfBirth, boolean status) {
        super(id, status);
        this.imageUrl = imageUrl;
        this.dateOfBirth = dateOfBirth;
    }

    public PersonDTO(PersonDTO other) {
        super(other); // Gọi constructor clone của BaseDTO
        if (other != null) {
            this.imageUrl = other.imageUrl;
            this.dateOfBirth = other.dateOfBirth != null ? new Date(other.dateOfBirth.getTime()) : null;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
