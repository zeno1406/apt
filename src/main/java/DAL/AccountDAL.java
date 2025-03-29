package DAL;

import DTO.AccountDTO;

import java.sql.*;

public class AccountDAL extends BaseDAL<AccountDTO, Integer> {
    private static final AccountDAL INSTANCE = new AccountDAL();

    private AccountDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "account", "employee_id");
    }

    public static AccountDAL getInstance() {
        return INSTANCE;
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
    protected boolean shouldUseGeneratedKeys() {
        return false;
    }

    @Override
    protected String getInsertQuery() {
        return "(employee_id, username, password) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, AccountDTO obj) throws SQLException {
        statement.setInt(1, obj.getEmployeeId());
        statement.setString(2, obj.getUsername());
        statement.setString(3, obj.getPassword());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET password = ? WHERE employee_id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, AccountDTO obj) throws SQLException {
        statement.setString(1, obj.getPassword());
        statement.setInt(2, obj.getEmployeeId());
    }
}
