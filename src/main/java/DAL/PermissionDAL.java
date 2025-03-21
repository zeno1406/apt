package DAL;
import DTO.PermissionDTO;
import java.sql.*;

public class PermissionDAL extends BaseDAL<PermissionDTO, Integer> {
    private static final PermissionDAL INSTANCE = new PermissionDAL();
    private PermissionDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "permission", "id");
    }

    public static PermissionDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(PermissionDTO obj) {
        throw new UnsupportedOperationException("Cannot insert permission records.");
    }

    @Override
    public boolean delete(Integer id) {
        throw new UnsupportedOperationException("Cannot delete permission records.");
    }

    @Override
    public boolean update(PermissionDTO obj) {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }


    @Override
    protected PermissionDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new PermissionDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getInt("module_id")
        );
    }
}
