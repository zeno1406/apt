package DTO;

import java.util.Date;

public class DiscountDTO {
    private String code;
    private String name;
    private int type;
    private Date startDate;
    private Date endDate;

    // Constructors
    public DiscountDTO() {
    }

    public DiscountDTO(String code, String name, int type, Date startDate, Date endDate) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
