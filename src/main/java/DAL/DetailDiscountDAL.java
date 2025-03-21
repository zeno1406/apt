package DAL;

import DTO.DetailDiscountDTO;
import java.sql.*;

/*
+ Remain getAll
+ Remain getById
+ Override insert - Not Need GENERATED_KEYS
+ Remain update - Update total_price_invoice and discount_amount
+ Remain delete (hard delete)
+ Need Insert ArrayList
*/

public class DetailDiscountDAL extends BaseDAL<DetailDiscountDTO, String> {
    public static final DetailDiscountDAL INSTANCE = new DetailDiscountDAL();

    private DetailDiscountDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "detail_discount", "discount_code");
    }

    public static DetailDiscountDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DetailDiscountDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DetailDiscountDTO(
                resultSet.getString("discount_code"),
                resultSet.getBigDecimal("total_price_invoice"),
                resultSet.getBigDecimal("discount_amount")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(discount_code, total_price_invoice, discount_amount) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DetailDiscountDTO obj) throws SQLException {
        statement.setString(1, obj.getDiscountCode());
        statement.setBigDecimal(2, obj.getTotalPriceInvoice());
        statement.setBigDecimal(3, obj.getDiscountAmount());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET total_price_invoice = ?, discount_amount = ? WHERE discount_code = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, DetailDiscountDTO obj) throws SQLException {
        statement.setBigDecimal(1, obj.getTotalPriceInvoice());
        statement.setBigDecimal(2, obj.getDiscountAmount());
        statement.setString(3, obj.getDiscountCode());
    }
}
