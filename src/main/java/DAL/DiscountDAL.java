package DAL;
import DTO.DiscountDTO;
import java.sql.*;

public class DiscountDAL extends BaseDAL<DiscountDTO, String> {
    private static final DiscountDAL INSTANCE = new DiscountDAL();

    private DiscountDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "discount", "code");
    }

    public static DiscountDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(DiscountDTO obj) {
        final String query = "INSERT INTO " + table + " (code, name, type, startDate, endDate) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setInsertParameters(statement, obj);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting discount: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ?, startDate = ?, endDate = ? WHERE code = ?";
    }

    @Override
    protected DiscountDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DiscountDTO(
                resultSet.getString("code"),
                resultSet.getString("name"),
                resultSet.getInt("type"),
                resultSet.getTimestamp("startDate") != null ? resultSet.getTimestamp("startDate").toLocalDateTime() : null,
                resultSet.getTimestamp("endDate") != null ? resultSet.getTimestamp("endDate").toLocalDateTime() : null
        );
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DiscountDTO obj) throws SQLException {
        statement.setString(1, obj.getCode());
        statement.setString(2, obj.getName());
        statement.setInt(3, obj.getType());
        statement.setTimestamp(4, obj.getStartDate() != null ? Timestamp.valueOf(obj.getStartDate()) : null);
        statement.setTimestamp(5, obj.getEndDate() != null ? Timestamp.valueOf(obj.getEndDate()) : null);
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, DiscountDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setTimestamp(2, obj.getStartDate() != null ? Timestamp.valueOf(obj.getStartDate()) : null);
        statement.setTimestamp(3, obj.getEndDate() != null ? Timestamp.valueOf(obj.getEndDate()) : null);
        statement.setString(4, obj.getCode());
    }

    public boolean update(DiscountDTO obj, String newCode) {
        if (newCode == null || newCode.trim().isEmpty()) {
            System.err.println("Error: newCode cannot be null or empty.");
            return false;
        }

        final String query = "UPDATE discount SET code = ?, name = ?, type = ?, startDate = ?, endDate = ? WHERE code = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, newCode);
            statement.setString(2, obj.getName());
            statement.setInt(3, obj.getType());
            statement.setTimestamp(4, obj.getStartDate() != null ? Timestamp.valueOf(obj.getStartDate()) : null);
            statement.setTimestamp(5, obj.getEndDate() != null ? Timestamp.valueOf(obj.getEndDate()) : null);
            statement.setString(6, obj.getCode());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating discount: " + e.getMessage());
            return false;
        }
    }
}
