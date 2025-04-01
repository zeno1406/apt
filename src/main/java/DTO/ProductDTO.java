package DTO;

import java.math.BigDecimal;

public class ProductDTO {
    private String id;
    private String name;
    private int stockQuantity;
    private BigDecimal sellingPrice;
    private boolean status;
    private String description;
    private String imageUrl;
    private int categoryId;

    public ProductDTO() {
    }

    public ProductDTO(String id, String name, int stockQuantity, BigDecimal sellingPrice, boolean status,
                      String description, String imageUrl, int categoryId) {
        this.id = id;
        this.name = name;
        this.stockQuantity = stockQuantity;
        this.sellingPrice = sellingPrice;
        this.status = status;
        this.description = description;
        this.imageUrl = imageUrl;
        this.categoryId = categoryId;
    }

    public ProductDTO(ProductDTO product) {
        this.id = product.id;
        this.name = product.name;
        this.stockQuantity = product.stockQuantity;
        this.sellingPrice = product.sellingPrice;
        this.status = product.status;
        this.description = product.description;
        this.imageUrl = product.imageUrl;
        this.categoryId = product.categoryId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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