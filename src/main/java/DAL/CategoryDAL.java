package DAL;

import DTO.CategoryDTO;

import java.sql.*;

public class CategoryDAL extends BaseDAL<CategoryDTO, Integer> {
    public static final CategoryDAL INSTANCE = new CategoryDAL();
    private CategoryDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "category", "id");
    }

    public static CategoryDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(CategoryDTO obj) {
        final String query = "INSERT INTO category (name, status) VALUES (?, ?)";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            setInsertParameters(statement, obj);
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                return false;
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    obj.setId(generatedKeys.getInt(1));
                }
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting module: " + e.getMessage());
            return false;
        }
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
    protected String getUpdateQuery() {
        return "SET name = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, CategoryDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setBoolean(2, obj.isStatus());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, CategoryDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setBoolean(2, obj.isStatus());
        statement.setInt(3, obj.getId());
    }
}
