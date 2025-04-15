package DAL;

import DTO.DetailInvoiceDTO;
import DTO.ProductDTO;
import java.sql.*;
import java.util.ArrayList;

public class ProductDAL extends BaseDAL<ProductDTO, String> {
    public static final ProductDAL INSTANCE = new ProductDAL();
    private ProductDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "product", "id");
    }

    public static ProductDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected ProductDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new ProductDTO(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getInt("stock_quantity"),
                resultSet.getBigDecimal("selling_price"),
                resultSet.getBoolean("status"),
                resultSet.getString("description"),
                resultSet.getString("image_url"),
                resultSet.getInt("category_id")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(id, name, stock_quantity, selling_price, status, description, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, ProductDTO obj) throws SQLException {
        statement.setString(1, obj.getId());
        statement.setString(2, obj.getName());
        statement.setInt(3, obj.getStockQuantity());
        statement.setBigDecimal(4, obj.getSellingPrice());
        statement.setBoolean(5, obj.isStatus());
        statement.setString(6, obj.getDescription());
        statement.setString(7, obj.getImageUrl());
        statement.setInt(8, obj.getCategoryId());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ?, stock_quantity = ?, selling_price = ?, status = ?, description = ?, image_url = ?, category_id = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, ProductDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setInt(2, obj.getStockQuantity());
        statement.setBigDecimal(3, obj.getSellingPrice());
        statement.setBoolean(4, obj.isStatus());
        statement.setString(5, obj.getDescription());
        statement.setString(6, obj.getImageUrl());
        statement.setInt(7, obj.getCategoryId());
        statement.setString(8, obj.getId());
    }

    @Override
    protected boolean hasSoftDelete() {
        return true;
    }

    public boolean updateProductQuantity(ProductDTO obj) {
        String query = "UPDATE product SET stock_quantity = ? WHERE id = ?";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, obj.getStockQuantity());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating stock quantity product: " + e.getMessage());
            return false;
        }
    }

    public boolean insertListProductExcel(ArrayList<ProductDTO> list) {
        final String query = "INSERT INTO product (id, name, stock_quantity, selling_price, status, description, image_url, category_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (ProductDTO obj : list) {
                statement.setString(1, obj.getId());
                statement.setString(2, obj.getName());
                statement.setInt(3, obj.getStockQuantity());
                statement.setBigDecimal(4, obj.getSellingPrice());
                statement.setBoolean(5, obj.isStatus());
                statement.setString(6, obj.getDescription());
                statement.setString(7, obj.getImageUrl());
                statement.setInt(8, obj.getCategoryId());
                statement.addBatch();
            }

            int[] results = statement.executeBatch();

            for (int result : results) {
                if (result < 0) {
                    return false;
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error inserting detail invoice: " + e.getMessage());
            return false;
        }
    }
}
