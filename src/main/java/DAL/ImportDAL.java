package DAL;

import DTO.ImportDTO;
import java.sql.*;

public class ImportDAL extends BaseDAL<ImportDTO, Integer> {
    public static final ImportDAL INSTANCE = new ImportDAL();

    private ImportDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "import", "id");
    }

    public static ImportDAL getInstance() {
        return INSTANCE;
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
    protected boolean shouldUseGeneratedKeys() {
        return true;
    }

    @Override
    protected void setGeneratedKey(ImportDTO obj, ResultSet generatedKeys) throws SQLException {
        if (generatedKeys.next()) {
            obj.setId(generatedKeys.getInt(1));
        }
    }

    @Override
    protected String getInsertQuery() {
        return "(create_date, employee_id, supplier_id, total_price) VALUES (?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, ImportDTO obj) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(obj.getCreateDate()));
        statement.setInt(2, obj.getEmployeeId());
        statement.setInt(3, obj.getSupplierId());
        statement.setBigDecimal(4, obj.getTotalPrice());
    }

    @Override
    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }

}
