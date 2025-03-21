package DAL;

import DTO.DetailImportDTO;
import java.sql.*;

/*
+ Remain getAll
+ Remain getById
+ Override insert - Not Need GENERATED_KEYS
+ Override update - Not allowed
+ Override delete - Hard delete (allowed)
+ Need Insert ArrayList
*/

public class DetailImportDAL extends BaseDAL<DetailImportDTO, Integer> {
    public static final DetailImportDAL INSTANCE = new DetailImportDAL();

    private DetailImportDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "detail_import", "import_id");
    }

    public static DetailImportDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DetailImportDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DetailImportDTO(
                resultSet.getInt("import_id"),
                resultSet.getString("product_id"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("price"),
                resultSet.getBigDecimal("profit_percent"),
                resultSet.getBigDecimal("total_price")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(import_id, product_id, quantity, price, profit_percent, total_price) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DetailImportDTO obj) throws SQLException {
        statement.setInt(1, obj.getImportId());
        statement.setString(2, obj.getProductId());
        statement.setInt(3, obj.getQuantity());
        statement.setBigDecimal(4, obj.getPrice());
        statement.setBigDecimal(5, obj.getProfitPercent());
        statement.setBigDecimal(6, obj.getTotalPrice());
    }

    @Override
    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }

    @Override
    protected boolean hasSoftDelete() {
        throw new UnsupportedOperationException("Cannot delete permission records.");
    }
}
