package DAL;

import DTO.ImportDTO;
import java.sql.*;

public class ImportDAL extends BaseDAL<ImportDTO, Integer> {
    public static final ImportDAL INSTANCE = new ImportDAL();

    private ImportDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "import", "id");
    }

    public static ImportDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(ImportDTO obj) {
        final String query = "INSERT INTO " + table + " (create_date, employee_id, supplier_id, total_price) VALUES (?, ?, ?, ?)";
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
            System.err.println("Error inserting import record: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected ImportDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new ImportDTO(
                resultSet.getInt("id"),
                resultSet.getTimestamp("create_date") != null
                        ? resultSet.getTimestamp("create_date").toLocalDateTime()
                        : null,
                resultSet.getInt("employee_id"),
                resultSet.getInt("supplier_id"),
                resultSet.getBigDecimal("total_price")
        );
    }

    @Override
    public boolean update(ImportDTO obj) {
        throw new UnsupportedOperationException("Update operation not supported for import records.");
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, ImportDTO obj) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(obj.getCreateDate()));
        statement.setInt(2, obj.getEmployeeId());
        statement.setInt(3, obj.getSupplierId());
        statement.setBigDecimal(4, obj.getTotalPrice());
    }
}
