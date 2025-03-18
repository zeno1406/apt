package DAL;

import DTO.AccountDTO;
import DTO.ModuleDTO;

import java.sql.*;

public class AccountDAL extends BaseDAL<AccountDTO, Integer> {
    private static final AccountDAL INSTANCE = new AccountDAL();
    private AccountDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "account", "employee_id");
    }

    private static AccountDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(AccountDTO obj) {
        final String query = "INSERT INTO " + table + " (employee_id, username, password) VALUES (?, ?, ?)";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setInsertParameters(statement, obj);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting module: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected String getUpdateQuery() {
        return "SET password = ? WHERE employee_id = ?";
    }

    @Override
    protected AccountDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new AccountDTO(
                resultSet.getInt("employee_id"),
                resultSet.getString("username"),
                resultSet.getString("password")
        );
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, AccountDTO obj) throws SQLException {
        statement.setInt(1, obj.getEmployeeId());
        statement.setString(2, obj.getUsername());
        statement.setString(3, obj.getPassword());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, AccountDTO obj) throws SQLException {
        statement.setString(1, obj.getPassword());
        statement.setInt(2, obj.getEmployeeId());
    }

}
