package DAL;

import DTO.DetailProductDTO;
import java.sql.*;

public class DetailProductDAL extends BaseDAL<DetailProductDTO, String> {
    public static final DetailProductDAL INSTANCE = new DetailProductDAL();

    private DetailProductDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "detail_product", "product_id");
    }

    public static DetailProductDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DetailProductDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DetailProductDTO(
                resultSet.getString("product_id"),
                resultSet.getString("description"),
                resultSet.getString("image_url"),
                resultSet.getInt("category_id"),
                resultSet.getInt("supplier_id")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(product_id, description, image_url, category_id, supplier_id) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DetailProductDTO obj) throws SQLException {
        statement.setString(1, obj.getProductId());
        statement.setString(2, obj.getDescription());
        statement.setString(3, obj.getImageUrl());
        statement.setInt(4, obj.getCategoryId());
        statement.setInt(5, obj.getSupplierId());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET description = ?, image_url = ?, category_id = ?, supplier_id = ? WHERE product_id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, DetailProductDTO obj) throws SQLException {
        statement.setString(1, obj.getDescription());
        statement.setString(2, obj.getImageUrl());
        statement.setInt(3, obj.getCategoryId());
        statement.setInt(4, obj.getSupplierId());
        statement.setString(5, obj.getProductId());
    }
}
