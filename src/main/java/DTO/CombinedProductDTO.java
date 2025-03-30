package DTO;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CombinedProductDTO {
    private String productId;
    private String name;
    private int stockQuantity;
    private BigDecimal sellingPrice;
    private boolean status;
    private String description;
    private String imageUrl;
    private int categoryId;

    public CombinedProductDTO(ProductDTO product, DetailProductDTO detail) {
        this.productId = product.getId();
        this.name = product.getName();
        this.stockQuantity = product.getStockQuantity();
        this.sellingPrice = product.getSellingPrice();
        this.status = product.isStatus();

        if (detail != null) {
            this.description = detail.getDescription();
            this.imageUrl = detail.getImageUrl();
            this.categoryId = detail.getCategoryId();
        } else {
            this.description = "Không có";
            this.imageUrl = "";
            this.categoryId = 0;
        }
    }
}
