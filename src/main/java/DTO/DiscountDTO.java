package DTO;

import lombok.ToString;

import java.time.LocalDateTime;

@ToString
public class DiscountDTO {
    private String code;
    private String name;
    private int type;
    private LocalDateTime startDate;
    private LocalDateTime  endDate;

    // Constructors
    public DiscountDTO() {
    }

    public DiscountDTO(String code, String name, int type, LocalDateTime  startDate, LocalDateTime  endDate) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public DiscountDTO(DiscountDTO discountDTO) {
        this.code = discountDTO.code;
        this.name = discountDTO.name;
        this.type = discountDTO.type;
        this.startDate = discountDTO.startDate;
        this.endDate = discountDTO.endDate;
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

    public LocalDateTime  getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime  startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime  getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime  endDate) {
        this.endDate = endDate;
    }
}
