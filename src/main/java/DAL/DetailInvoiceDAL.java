package DAL;

import DTO.DetailImportDTO;
import DTO.DetailInvoiceDTO;
import java.sql.*;

/*
+ Remain getAll
+ Remain getById
+ Override insert - Not Need GENERATED_KEYS
+ Override update - Update product_id, quantity, price, and total_price
+ Override delete - Allowed (hard delete)
+ Need Insert ArrayList
*/

public class DetailInvoiceDAL extends BaseDAL<DetailInvoiceDTO, Integer> {
    public static final DetailInvoiceDAL INSTANCE = new DetailInvoiceDAL();

    private DetailInvoiceDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "detail_invoice", "invoice_id");
    }

    public static DetailInvoiceDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DetailInvoiceDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DetailInvoiceDTO(
                resultSet.getInt("invoice_id"),
                resultSet.getString("product_id"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("price"),  // Đổi lại đúng tên cột
                resultSet.getBigDecimal("total_price")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(invoice_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DetailInvoiceDTO obj) throws SQLException {
        statement.setInt(1, obj.getInvoiceId());
        statement.setString(2, obj.getProductId());
        statement.setInt(3, obj.getQuantity());
        statement.setBigDecimal(4, obj.getPrice());
        statement.setBigDecimal(5, obj.getTotalPrice());
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
