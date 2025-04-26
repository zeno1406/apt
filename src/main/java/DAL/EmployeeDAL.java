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
                resultSet.getDate("date_of_birth") != null
                        ? resultSet.getDate("date_of_birth").toLocalDate().atStartOfDay()
                        : null,
                resultSet.getInt("role_id"),
                resultSet.getBoolean("status")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(first_name, last_name, salary, date_of_birth, role_id, status) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, EmployeeDTO obj) throws SQLException {
        statement.setString(1, obj.getFirstName());
        statement.setString(2, obj.getLastName());
        statement.setBigDecimal(3, obj.getSalary());
        statement.setDate(4, obj.getDateOfBirth() != null ? java.sql.Date.valueOf(obj.getDateOfBirth().toLocalDate()) : null);
        statement.setInt(5, obj.getRoleId());
        statement.setBoolean(6, obj.isStatus());
    }

    @Override
    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Cannot update Employee records.");
    }


    @Override
    protected boolean hasSoftDelete() {
        return true;
    }

    public boolean updateAdvance(EmployeeDTO obj, boolean allowAdvanceChange) {
        String query = allowAdvanceChange
                ? "UPDATE employee SET first_name = ?, last_name = ?, salary = ?, date_of_birth = ?, role_id = ?, status = ? WHERE id = ?"
                : "UPDATE employee SET first_name = ?, last_name = ?, salary = ?, date_of_birth = ? WHERE id = ?";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, obj.getFirstName());
            statement.setString(2, obj.getLastName());
            statement.setBigDecimal(3, obj.getSalary());
            statement.setDate(4, obj.getDateOfBirth() != null ? java.sql.Date.valueOf(obj.getDateOfBirth().toLocalDate()) : null);

            if (allowAdvanceChange) {
                statement.setInt(5, obj.getRoleId());
                statement.setBoolean(6, obj.isStatus());
                statement.setInt(7, obj.getId());
            } else {
                statement.setInt(5, obj.getId());
            }

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating advance employee: " + e.getMessage());
            return false;
        }
    }

    public boolean updateBasic(EmployeeDTO obj, boolean allowAdvanceChange) {
        String query = allowAdvanceChange
                ? "UPDATE employee SET first_name = ?, last_name = ?, date_of_birth = ?, role_id = ?, status = ? WHERE id = ?"
                : "UPDATE employee SET first_name = ?, last_name = ?, date_of_birth = ? WHERE id = ?";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, obj.getFirstName());
            statement.setString(2, obj.getLastName());
            statement.setDate(3, obj.getDateOfBirth() != null ? java.sql.Date.valueOf(obj.getDateOfBirth().toLocalDate()) : null);

            if (allowAdvanceChange) { // Chỉ cập nhật role_id và status nếu được phép
                statement.setInt(4, obj.getRoleId());
                statement.setBoolean(5, obj.isStatus());
                statement.setInt(6, obj.getId());
            } else {
                statement.setInt(4, obj.getId());
            }

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating basic employee: " + e.getMessage());
            return false;
        }
    }



}
