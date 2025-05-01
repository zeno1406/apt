package DTO;

import java.math.BigDecimal;

public class StatisticDTO {
    public static class ProductRevenue {
        private String productId;
        private String productName;
        private String categoryName;
        private int totalQuantity;

        public ProductRevenue(String productId, String productName, String categoryName, int totalQuantity) {
            this.productId = productId;
            this.productName = productName;
            this.categoryName = categoryName;
            this.totalQuantity = totalQuantity;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }
        public void setTotalQuantity(int totalQuantity) {
            this.totalQuantity = totalQuantity;
        }
    }

    public static class QuarterlyEmployeeRevenue {
        private int employeeId;
        private BigDecimal quarter1;
        private BigDecimal quarter2;
        private BigDecimal quarter3;
        private BigDecimal quarter4;
        private BigDecimal revenue;

        public QuarterlyEmployeeRevenue(int employeeId, BigDecimal quarter1, BigDecimal quarter2,
                                        BigDecimal quarter3, BigDecimal quarter4) {
            this.employeeId = employeeId;
            this.quarter1 = quarter1 != null ? quarter1 : BigDecimal.ZERO;
            this.quarter2 = quarter2 != null ? quarter2 : BigDecimal.ZERO;
            this.quarter3 = quarter3 != null ? quarter3 : BigDecimal.ZERO;
            this.quarter4 = quarter4 != null ? quarter4 : BigDecimal.ZERO;
            this.revenue = this.quarter1.add(this.quarter2).add(this.quarter3).add(this.quarter4);
        }

        public int getEmployeeId() {
            return employeeId;
        }

        public void setEmployeeId(int employeeId) {
            this.employeeId = employeeId;
        }

        public BigDecimal getQuarter1() {
            return quarter1;
        }

        public void setQuarter1(BigDecimal quarter1) {
            this.quarter1 = quarter1;
            updateRevenue();
        }

        public BigDecimal getQuarter2() {
            return quarter2;
        }

        public void setQuarter2(BigDecimal quarter2) {
            this.quarter2 = quarter2;
            updateRevenue();
        }

        public BigDecimal getQuarter3() {
            return quarter3;
        }

        public void setQuarter3(BigDecimal quarter3) {
            this.quarter3 = quarter3;
            updateRevenue();
        }

        public BigDecimal getQuarter4() {
            return quarter4;
        }

        public void setQuarter4(BigDecimal quarter4) {
            this.quarter4 = quarter4;
            updateRevenue();
        }

        public BigDecimal getRevenue() {
            return revenue;
        }

        private void updateRevenue() {
            this.revenue = this.quarter1.add(this.quarter2).add(this.quarter3).add(this.quarter4);
        }
    }
}