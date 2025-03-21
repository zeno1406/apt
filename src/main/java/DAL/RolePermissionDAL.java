package DAL;
import DTO.RolePermissionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RolePermissionDAL extends BaseDAL<RolePermissionDTO, Integer> {
    private static final RolePermissionDAL INSTANCE = new RolePermissionDAL();

    private RolePermissionDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "role_permission", "role_id");
    }

    public static RolePermissionDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected RolePermissionDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new RolePermissionDTO (
                resultSet.getInt("role_id"),
                resultSet.getInt("permission_id"),
                resultSet.getBoolean("status")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(role_id, permission_id, status) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, RolePermissionDTO obj) throws SQLException {
        statement.setInt(1, obj.getRoleId());
        statement.setInt(2, obj.getPermissionId());
        statement.setBoolean(3, obj.isStatus());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET status = ? WHERE role_id = ? AND permission_id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, RolePermissionDTO obj) throws SQLException {
        statement.setBoolean(1, obj.isStatus());
        statement.setInt(2, obj.getRoleId());
        statement.setInt(3, obj.getPermissionId());
    }

    @Override
    protected boolean hasSoftDelete() {
        throw new UnsupportedOperationException("Cannot delete role permission records.");
    }

    public boolean insertDefaultRolePermissionByRoleId(int roleId) {
        final String query = "INSERT INTO role_permission (role_id, permission_id, status) SELECT ?, id, 0 FROM permission";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, roleId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting default permissions: " + e.getMessage());
            return false;
        }
    }

    public boolean insertRollbackPermission(ArrayList<RolePermissionDTO> rolePermission) {
        final String query = "INSERT INTO role_permission (role_id, permission_id, status) VALUES (?, ?, ?)";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            boolean success = true;
            for (RolePermissionDTO permission : rolePermission) {
                statement.setInt(1, permission.getRoleId());
                statement.setInt(2, permission.getPermissionId());
                statement.setBoolean(3, permission.isStatus());

                if (statement.executeUpdate() <= 0) {
                    success = false; // Nếu có bản ghi nào không chèn được thì đánh dấu thất bại
                }
            }
            return success;
        } catch (SQLException e) {
            System.err.println("Error roll back role permissions: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRolePermissionByRoleId(int roleId) {
        final String query = "DELETE FROM role_permission WHERE role_id = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, roleId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting from " + table + ": " + e.getMessage());
            return false;
        }
    }

    public ArrayList<RolePermissionDTO> getAllRolePermissionByRoleId(int roleID) {
        final String query = "SELECT * FROM role_permission WHERE role_id = ?";
        ArrayList<RolePermissionDTO> list = new ArrayList<>();

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, roleID);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapResultSetToObject(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving " + table + ": " + e.getMessage());
        }
        return list;
    }

    public boolean updateRolePermission(RolePermissionDTO rolePermission) {
        final String query = "UPDATE role_permission SET status = ? WHERE role_id = ? AND permission_id = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBoolean(1, rolePermission.isStatus());
            statement.setInt(2, rolePermission.getRoleId());
            statement.setInt(3, rolePermission.getPermissionId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating role_permission: " + e.getMessage());
            return false;
        }
    }
}
