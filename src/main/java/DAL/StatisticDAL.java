package DAL;

import DTO.StatisticDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatisticDAL extends BaseDAL<StatisticDTO, Integer> {
    private static final StatisticDAL INSTANCE = new StatisticDAL();

    private StatisticDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "statistic", "invoice_id");
    }

    public static StatisticDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected StatisticDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new StatisticDTO(
                resultSet.getTimestamp("save_date").toLocalDateTime(),
                resultSet.getInt("invoice_id"),
                resultSet.getBigDecimal("total_capital")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(save_date, invoice_id, total_capital) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, StatisticDTO obj) throws SQLException {
        statement.setTimestamp(1, java.sql.Timestamp.valueOf(obj.getSaveDate()));
        statement.setInt(2, obj.getInvoiceId());
        statement.setBigDecimal(3, obj.getTotalCapital());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET total_capital = ? WHERE save_date = ? AND invoice_id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, StatisticDTO obj) throws SQLException {
        statement.setBigDecimal(1, obj.getTotalCapital());
        statement.setTimestamp(2, java.sql.Timestamp.valueOf(obj.getSaveDate()));
        statement.setInt(3, obj.getInvoiceId());
    }

    @Override
    protected boolean hasSoftDelete() {
        throw new UnsupportedOperationException("Cannot delete statistics records.");
    }
}
