package DAL;

import DTO.InvoiceDTO;

import java.math.BigDecimal;
import java.sql.*;

public class InvoiceDAL extends BaseDAL<InvoiceDTO, Integer> {
    public static final InvoiceDAL INSTANCE = new InvoiceDAL();

    private InvoiceDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "invoice", "id");
    }

    public static InvoiceDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected InvoiceDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new InvoiceDTO(
                resultSet.getInt("id"),
                resultSet.getTimestamp("create_date") != null
                        ? resultSet.getTimestamp("create_date").toLocalDateTime()
                        : null,
                resultSet.getInt("employee_id"),
                resultSet.getInt("customer_id"),
                resultSet.getString("discount_code"),
                resultSet.getBigDecimal("discount_amount") != null ? resultSet.getBigDecimal("discount_amount") : BigDecimal.ZERO,
                resultSet.getBigDecimal("total_price")
        );
    }

    @Override
    protected boolean shouldUseGeneratedKeys() {
        return true; // ID là AUTO_INCREMENT
    }

    @Override
    protected void setGeneratedKey(InvoiceDTO obj, ResultSet generatedKeys) throws SQLException {
        if (generatedKeys.next()) {
            obj.setId(generatedKeys.getInt(1));
        }
    }

    @Override
    protected String getInsertQuery() {
        return "(create_date, employee_id, customer_id, discount_code, discount_amount, total_price) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, InvoiceDTO obj) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(obj.getCreateDate()));
        statement.setInt(2, obj.getEmployeeId());
        statement.setInt(3, obj.getCustomerId());

        // Xử lý discount_code có thể null
        if (obj.getDiscountCode() != null) {
            statement.setString(4, obj.getDiscountCode());
        } else {
            statement.setNull(4, Types.VARCHAR);
        }

        // Luôn đặt giá trị cho discountAmount
        statement.setBigDecimal(5, obj.getDiscountAmount() != null ? obj.getDiscountAmount() : BigDecimal.ZERO);

        statement.setBigDecimal(6, obj.getTotalPrice());
    }

    @Override
    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }

}
