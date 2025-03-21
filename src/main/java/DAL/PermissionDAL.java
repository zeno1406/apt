package DAL;
import DTO.PermissionDTO;
import DTO.RolePermissionDTO;

import java.sql.*;
import java.util.ArrayList;

public class PermissionDAL extends BaseDAL<PermissionDTO, Integer> {
    private static final PermissionDAL INSTANCE = new PermissionDAL();
    private PermissionDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "permission", "id");
    }

    public static PermissionDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected PermissionDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new PermissionDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("module_id")
        );
    }

    @Override
    protected String getInsertQuery() {
        throw new UnsupportedOperationException("Cannot insert permission records.");
    }

    @Override
    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }

    @Override
    protected boolean hasSoftDelete() {
        throw new UnsupportedOperationException("Cannot delete permission records.");
    }

    public ArrayList<PermissionDTO> getAllRoleByModuleId(int moduleId) {
        final String query = "SELECT * FROM permission WHERE module_id = ?";
        ArrayList<PermissionDTO> list = new ArrayList<>();

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, moduleId);
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
}
