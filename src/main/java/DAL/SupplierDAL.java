package DAL;
import DTO.SupplierDTO;

import java.sql.*;

public class SupplierDAL extends BaseDAL<SupplierDTO, Integer> {
    public static final SupplierDAL INSTANCE = new SupplierDAL();
    private SupplierDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "supplier", "id");
    }

    public static SupplierDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(SupplierDTO obj) {
        final String query = "INSERT INTO " + table + " (name, phone, address, status) VALUES (?, ?, ?, ?)";
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
    protected String getUpdateQuery() {
        return "SET name = ?, phone = ?, address = ?, status = ? WHERE id = ?";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, SupplierDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getPhone());
        statement.setString(3, obj.getAddress());
        statement.setBoolean(4, obj.isStatus());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, SupplierDTO obj) throws SQLException {
        statement.setString(1, obj.getName());
        statement.setString(2, obj.getPhone());
        statement.setString(3, obj.getAddress());
        statement.setBoolean(4, obj.isStatus());
        statement.setInt(5, obj.getId());
    }
}
