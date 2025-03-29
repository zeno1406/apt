package DTO;

import lombok.ToString;

@ToString
public class DetailProductDTO {
    private String productId;
    private String description;
    private String imageUrl;
    private int categoryId;

    public DetailProductDTO() {
    }

    public DetailProductDTO(String productId, String description, String imageUrl, int categoryId) {
        this.productId = productId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    public DetailProductDTO(DetailProductDTO other) {
        this.productId = other.productId;
        this.description = other.description;
        this.imageUrl = other.imageUrl;
        this.categoryId = other.categoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

}
