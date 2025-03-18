package DAL;

import DTO.ModuleDTO;
import java.sql.*;

public class ModuleDAL extends BaseDAL <ModuleDTO, Integer> {
    public static final ModuleDAL INSTANCE = new ModuleDAL();
    private ModuleDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "module", "id");
    }

    public static ModuleDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(ModuleDTO obj) {
        final String query = "INSERT INTO " + table + " (name) VALUES (?)";
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
    protected ModuleDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new ModuleDTO(
                resultSet.getInt("id"),
                resultSet.getString("name")
        );
    }

    @Override
    protected String getUpdateQuery() {
        return "SET name = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, ModuleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, ModuleDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setInt(2, obj.getId());
    }
}
