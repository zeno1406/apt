package DAL;
import DTO.ModuleDTO;
import DTO.SupplierDTO;

import java.sql.*;

public class SupplierDAL extends BaseDAL<SupplierDTO, Integer> {
    public static final SupplierDAL INSTANCE = new SupplierDAL();
    private SupplierDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "supplier", "id");
    }

    public static SupplierDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected SupplierDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new SupplierDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("phone"),
                resultSet.getString("address"),
                resultSet.getBoolean("status")
        );
    }

    @Override
    protected boolean shouldUseGeneratedKeys() {
        return true;
    }

    @Override
    protected void setGeneratedKey(SupplierDTO obj, ResultSet generatedKeys) throws SQLException {
        if (generatedKeys.next()) {
            obj.setId(generatedKeys.getInt(1));
        }
    }
    @Override
    protected String getInsertQuery() {
        return "(name, phone, address, status) VALUES (?, ?, ?, ?)";
    }
    @Override
    protected void setInsertParameters(PreparedStatement statement, SupplierDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getPhone());
        statement.setString(3, obj.getAddress());
        statement.setBoolean(4, obj.isStatus());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ?, phone = ?, address = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, SupplierDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getPhone());
        statement.setString(3, obj.getAddress());
        statement.setBoolean(4, obj.isStatus());
        statement.setInt(5, obj.getId());
    }

    @Override
    protected boolean hasSoftDelete() {
        return true;
    }
}
