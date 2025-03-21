package DTO;

public abstract class BaseDTO {
    protected int id;
    protected boolean status;

    public BaseDTO() {
    }

    public BaseDTO(int id, boolean status) {
        this.id = id;
        this.status = status;
    }

    public BaseDTO(BaseDTO other) {
        if (other != null) {
            this.id = other.id;
            this.status = other.status;
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
}
