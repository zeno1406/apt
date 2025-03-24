package DAL;

import DTO.EmployeeDTO;
import java.sql.*;

public class EmployeeDAL extends BaseDAL<EmployeeDTO, Integer> {
    public static final EmployeeDAL INSTANCE = new EmployeeDAL();

    private EmployeeDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "employee", "id");
    }

    public static EmployeeDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected EmployeeDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new EmployeeDTO(
                resultSet.getInt("id"),
                resultSet.getString("first_name"),
                resultSet.getString("last_name"),
                resultSet.getBigDecimal("salary"),
                resultSet.getString("image_url"),
                resultSet.getDate("date_of_birth") != null
                        ? resultSet.getDate("date_of_birth").toLocalDate().atStartOfDay()
                        : null,
                resultSet.getInt("role_id"),
                resultSet.getBoolean("status")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(first_name, last_name, salary, image_url, date_of_birth, role_id, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, EmployeeDTO obj) throws SQLException {
        statement.setString(1, obj.getFirstName());
        statement.setString(2, obj.getLastName());
        statement.setBigDecimal(3, obj.getSalary());
        statement.setString(4, obj.getImageUrl());
        statement.setDate(5, obj.getDateOfBirth() != null ? java.sql.Date.valueOf(obj.getDateOfBirth().toLocalDate()) : null);
        statement.setInt(6, obj.getRoleId());
        statement.setBoolean(7, obj.isStatus());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET first_name = ?, last_name = ?, salary = ?, image_url = ?, date_of_birth = ?, role_id = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, EmployeeDTO obj) throws SQLException {
        statement.setString(1, obj.getFirstName());
        statement.setString(2, obj.getLastName());
        statement.setBigDecimal(3, obj.getSalary());
        statement.setString(4, obj.getImageUrl());
        statement.setDate(5, obj.getDateOfBirth() != null ? java.sql.Date.valueOf(obj.getDateOfBirth().toLocalDate()) : null);
        statement.setInt(6, obj.getRoleId());
        statement.setBoolean(7, obj.isStatus());
        statement.setInt(8, obj.getId());
    }

    @Override
    protected boolean hasSoftDelete() {
        return true;
    }
}
