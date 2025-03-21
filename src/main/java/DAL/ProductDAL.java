package DAL;
import DTO.ProductDTO;
import java.sql.*;

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
                resultSet.getBoolean("status")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(id, name, stock_quantity, selling_price, status) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, ProductDTO obj) throws SQLException {
        statement.setString(1, obj.getId());
        statement.setString(2, obj.getName());
        statement.setInt(3, obj.getStockQuantity());
        statement.setBigDecimal(4, obj.getSellingPrice());
        statement.setBoolean(5, obj.isStatus());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ?, stock_quantity = ?, selling_price = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, ProductDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setInt(2, obj.getStockQuantity());
        statement.setBigDecimal(3, obj.getSellingPrice());
        statement.setBoolean(4, obj.isStatus());
        statement.setString(5, obj.getId());
    }

    @Override
    protected boolean hasSoftDelete() {
        return true;
    }
}
