package DTO;

public class DetailProductDTO {
    private String productId;
    private String description;
    private String imageUrl;
    private int categoryId;
    private int supplierId;

    public DetailProductDTO() {
    }

    public DetailProductDTO(String productId, String description, String imageUrl, int categoryId, int supplierId) {
        this.productId = productId;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
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

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
}
