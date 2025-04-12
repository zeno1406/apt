package DAL;

import DTO.ModuleDTO;
import java.sql.*;

public class ModuleDAL extends BaseDAL<ModuleDTO, Integer> {
    public static final ModuleDAL INSTANCE = new ModuleDAL();

    private ModuleDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "module", "id");
    }

    public static ModuleDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected ModuleDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new ModuleDTO(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    @Override
    protected boolean shouldUseGeneratedKeys() {
        return true; // ID là AUTO_INCREMENT
    }

    @Override
    protected void setGeneratedKey(ModuleDTO obj, ResultSet generatedKeys) throws SQLException {
        obj.setId(generatedKeys.getInt(1));
    }

    @Override
    protected String getInsertQuery() {
        return "(name) VALUES (?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, ModuleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, ModuleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setInt(2, obj.getId());
    }
}
