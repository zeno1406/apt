package DAL;

import DTO.RoleDTO;
import DTO.RolePermissionDTO;

import java.sql.*;

public class RoleDAL extends BaseDAL<RoleDTO, Integer> {
    private static final RoleDAL INSTANCE = new RoleDAL();

    private RoleDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "role", "id");
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
    protected String getUpdateQuery() {
        return "SET name = ?, description = ?, salary_coefficient = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, RoleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getDescription());
        statement.setBigDecimal(3, obj.getSalaryCoefficient());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, RoleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getDescription());
        statement.setBigDecimal(3, obj.getSalaryCoefficient());
        statement.setInt(4, obj.getId());
    }

    @Override
    public boolean insert(RoleDTO obj) {
        final String insertRoleQuery = "INSERT INTO role (name, description, salary_coefficient) VALUES (?, ?, ?)";

        try (Connection connection = connectionFactory.newConnection()) {
            connection.setAutoCommit(false); // Bắt đầu transaction

            try (PreparedStatement roleStmt = connection.prepareStatement(insertRoleQuery, Statement.RETURN_GENERATED_KEYS)) {
                setInsertParameters(roleStmt, obj);
                int affectedRows = roleStmt.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }

                try (ResultSet generatedKeys = roleStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newRoleId = generatedKeys.getInt(1);
                        obj.setId(newRoleId);

                        // Thêm tất cả quyền mặc định vào role_permission
                        RolePermissionDTO rolePermission = new RolePermissionDTO(newRoleId, -1, false);
                        if (!RolePermissionDAL.getInstance().insert(rolePermission)) {
                            connection.rollback();
                            return false;
                        }
                    }
                }
            }
            connection.commit(); // Hoàn tất transaction
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting role with permissions: " + e.getMessage());
            return false;
        }
    }



    @Override
    public boolean delete(Integer id) {
        final String deleteRoleQuery = "DELETE FROM role WHERE id = ?";

        try (Connection connection = connectionFactory.newConnection()) {
            connection.setAutoCommit(false);

            // Xóa quyền của role trước khi xóa role
            if (!RolePermissionDAL.getInstance().delete(id)) {
                connection.rollback();
                return false;
            }

            // Xóa role
            try (PreparedStatement roleStmt = connection.prepareStatement(deleteRoleQuery)) {
                roleStmt.setInt(1, id);
                int affectedRows = roleStmt.executeUpdate();

                if (affectedRows == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("Error deleting role: " + e.getMessage());
            return false;
        }
    }



}
