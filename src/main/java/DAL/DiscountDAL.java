package DAL;
import DTO.DiscountDTO;
import java.sql.*;

public class DiscountDAL extends BaseDAL<DiscountDTO, String> {
    private static final DiscountDAL INSTANCE = new DiscountDAL();

    private DiscountDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "discount", "code");
    }

    public static DiscountDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DiscountDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DiscountDTO(resultSet.getString("code"), resultSet.getString("name"), resultSet.getInt("type"),
                resultSet.getDate("startDate") != null ? resultSet.getDate("startDate").toLocalDate().atStartOfDay() : null,
                resultSet.getDate("endDate") != null ? resultSet.getDate("endDate").toLocalDate().atStartOfDay() : null
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(code, name, type, startDate, endDate) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DiscountDTO obj) throws SQLException {
        statement.setString(1, obj.getCode());
        statement.setString(2, obj.getName());
        statement.setInt(3, obj.getType());
        statement.setDate(4, obj.getStartDate() != null ? java.sql.Date.valueOf(obj.getStartDate().toLocalDate()) : null);
        statement.setDate(5, obj.getEndDate() != null ? java.sql.Date.valueOf(obj.getEndDate().toLocalDate()) : null);
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ?, type = ?, startDate = ?, endDate = ? WHERE code = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, DiscountDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setInt(2, obj.getType());
        statement.setDate(3, obj.getStartDate() != null ? java.sql.Date.valueOf(obj.getStartDate().toLocalDate()) : null);
        statement.setDate(4, obj.getEndDate() != null ? java.sql.Date.valueOf(obj.getEndDate().toLocalDate()) : null);
        statement.setString(5, obj.getCode());
    }
}
