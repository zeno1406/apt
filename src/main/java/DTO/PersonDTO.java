package DTO;

import java.util.Date;

public abstract class PersonDTO extends BaseDTO {
    protected String imageUrl;
    protected Date dateOfBirth;

    public PersonDTO() {
    }

    public PersonDTO(int id, String imageUrl, Date dateOfBirth, int status) {
        super(id, status);
        this.imageUrl = imageUrl;
        this.dateOfBirth = dateOfBirth;
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
