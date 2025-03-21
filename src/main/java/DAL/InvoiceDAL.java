package DAL;

import DTO.InvoiceDTO;

import java.sql.*;

public class InvoiceDAL extends BaseDAL<InvoiceDTO, Integer> {
    public static final InvoiceDAL INSTANCE = new InvoiceDAL();

    private InvoiceDAL() {
        super(ConnectAplication.getInstance().getConnectionFactory(), "invoice", "id");
    }

    public static InvoiceDAL getInstance() {
        return INSTANCE;
    }

    @Override
    public boolean insert(InvoiceDTO obj) {
        final String query = "INSERT INTO " + table +
                " (create_date, employee_id, customer_id, discount_code, total_price) VALUES (?, ?, ?, ?, ?)";
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
            System.err.println("Error inserting invoice: " + e.getMessage());
            return false;
        }
    }

    @Override
    protected InvoiceDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new InvoiceDTO(
                resultSet.getInt("id"),
                resultSet.getTimestamp("create_date") != null
                        ? resultSet.getTimestamp("create_date").toLocalDateTime()
                        : null,
                resultSet.getInt("employee_id"),
                resultSet.getInt("customer_id"),
                resultSet.getString("discount_code"),
                resultSet.getBigDecimal("total_price")
        );
    }

    @Override
    public boolean update(InvoiceDTO obj) {
        throw new UnsupportedOperationException("Update operation not supported.");
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, InvoiceDTO obj) throws SQLException {
        statement.setTimestamp(1, Timestamp.valueOf(obj.getCreateDate()));
        statement.setInt(2, obj.getEmployeeId());
        statement.setInt(3, obj.getCustomerId());

        // Xử lý discount_code có thể null
        if (obj.getDiscountCode() != null) {
            statement.setString(4, obj.getDiscountCode());
        } else {
            statement.setNull(4, Types.VARCHAR);
        }

        statement.setBigDecimal(5, obj.getTotalPrice());
    }
}
