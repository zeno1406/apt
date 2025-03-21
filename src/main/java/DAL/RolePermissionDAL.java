package DAL;
import DTO.RolePermissionDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RolePermissionDAL extends BaseDAL<RolePermissionDTO, Integer> {
    private static final RolePermissionDAL INSTANCE = new RolePermissionDAL();

    private RolePermissionDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "role_permission", "id");
    }

    public static RolePermissionDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean delete(Integer id) {
        final String query = "DELETE FROM role_permission WHERE role_id = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();  // Không cần kiểm tra số dòng bị ảnh hưởng
            return true;  // Xóa thành công ngay cả khi không có bản ghi nào
        } catch (SQLException e) {
            System.err.println("Error deleting role permissions: " + e.getMessage());
            return false;
        }
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
    protected String getUpdateQuery() {
        return "SET status = ? WHERE role_id = ? AND permission_id = ?";
    }

    @Override
    public boolean update(RolePermissionDTO obj) {
        final String query = "UPDATE " + table + " " + getUpdateQuery();
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setUpdateParameters(statement, obj);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating " + table + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, RolePermissionDTO obj) throws SQLException {
        statement.setBoolean(1, obj.isStatus());
        statement.setInt(2, obj.getRoleId());
        statement.setInt(3, obj.getPermissionId());
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, RolePermissionDTO obj) throws SQLException {
        statement.setInt(1, obj.getRoleId());
        statement.setInt(2, obj.getPermissionId());
        statement.setBoolean(3, obj.isStatus());
    }


    @Override
    public boolean insert(RolePermissionDTO obj) {
        final String query;

        if (obj.getPermissionId() != -1) {
            // Thêm một quyền cụ thể
            query = "INSERT INTO role_permission (role_id, permission_id, status) VALUES (?, ?, ?)";
        } else {
            // Thêm tất cả quyền mặc định
            query = "INSERT INTO role_permission (role_id, permission_id, status) " +
                    "SELECT ?, id, 0 FROM permission";
        }

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setInsertParameters(statement, obj);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting role_permission: " + e.getMessage());
            return false;
        }
    }


}
