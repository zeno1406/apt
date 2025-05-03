package DAL;

import DTO.RoleDTO;

import java.sql.*;

public class RoleDAL extends BaseDAL<RoleDTO, Integer> {
    private static final RoleDAL INSTANCE = new RoleDAL();

    private RoleDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "role", "id");
    }

    public static RoleDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected RoleDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new RoleDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("description"),
                resultSet.getBigDecimal("salary_coefficient")
        );
    }

    @Override
    protected boolean shouldUseGeneratedKeys() {
        return true;
    }

    @Override
    protected void setGeneratedKey(RoleDTO obj, ResultSet generatedKeys) throws SQLException {
        if (generatedKeys.next()) {
            obj.setId(generatedKeys.getInt(1));
        }
    }

    @Override
    protected String getInsertQuery() {
        return "(name, description, salary_coefficient) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, RoleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getDescription());
        statement.setBigDecimal(3, obj.getSalaryCoefficient());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ?, description = ?, salary_coefficient = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, RoleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getDescription());
        statement.setBigDecimal(3, obj.getSalaryCoefficient());
        statement.setInt(4, obj.getId());
    }

    public boolean updateBasic(RoleDTO obj) {
        String query = "UPDATE role SET name = ?, description = ? WHERE id = ?";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, obj.getName());
            statement.setString(2, obj.getDescription());
            statement.setInt(3, obj.getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating basic role: " + e.getMessage());
            return false;
        }
    }
}
