package DAL;

import DTO.CategoryDTO;
import java.sql.*;

public class CategoryDAL extends BaseDAL<CategoryDTO, Integer> {
    public static final CategoryDAL INSTANCE = new CategoryDAL();

    private CategoryDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "category", "id");
    }

    public static CategoryDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected CategoryDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new CategoryDTO(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getBoolean("status")
        );
    }

    @Override
    protected boolean shouldUseGeneratedKeys() {
        return true;
    }

    @Override
    protected void setGeneratedKey(CategoryDTO obj, ResultSet generatedKeys) throws SQLException {
        if (generatedKeys.next()) {
            obj.setId(generatedKeys.getInt(1));
        }
    }

    @Override
    protected String getInsertQuery() {
        return "(name, status) VALUES (?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, CategoryDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setBoolean(2, obj.isStatus());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, CategoryDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setBoolean(2, obj.isStatus());
        statement.setInt(3, obj.getId());
    }

    @Override
    protected boolean hasSoftDelete() {
        return true;
    }
}
