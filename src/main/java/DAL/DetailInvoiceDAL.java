package DAL;

import DTO.DetailImportDTO;
import DTO.DetailInvoiceDTO;
import java.sql.*;
import java.util.ArrayList;

/*
+ Remain getAll
+ Remain getById
+ Override insert - Not Need GENERATED_KEYS
+ Override update - Update product_id, quantity, price, and total_price
+ Override delete - Allowed (hard delete)
+ Need Insert ArrayList
*/

public class DetailInvoiceDAL extends BaseDAL<DetailInvoiceDTO, Integer> {
    public static final DetailInvoiceDAL INSTANCE = new DetailInvoiceDAL();

    private DetailInvoiceDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "detail_invoice", "invoice_id");
    }

    public static DetailInvoiceDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DetailInvoiceDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DetailInvoiceDTO(
                resultSet.getInt("invoice_id"),
                resultSet.getString("product_id"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("price"),  // Đổi lại đúng tên cột
                resultSet.getBigDecimal("total_price")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(invoice_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DetailInvoiceDTO obj) throws SQLException {
        statement.setInt(1, obj.getInvoiceId());
        statement.setString(2, obj.getProductId());
        statement.setInt(3, obj.getQuantity());
        statement.setBigDecimal(4, obj.getPrice());
        statement.setBigDecimal(5, obj.getTotalPrice());
    }

    @Override
    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }

    @Override
    protected boolean hasSoftDelete() {
        throw new UnsupportedOperationException("Cannot delete permission records.");
    }

    public boolean insertDetailInvoiceByInvoiceId(ArrayList<DetailInvoiceDTO> list) {
        final String query = "INSERT INTO detail_invoice (invoice_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (DetailInvoiceDTO detail : list) {
                statement.setInt(1, detail.getInvoiceId());
                statement.setString(2, detail.getProductId());
                statement.setInt(3, detail.getQuantity());
                statement.setBigDecimal(4, detail.getPrice());
                statement.setBigDecimal(5, detail.getTotalPrice());
                statement.addBatch();
            }

            int[] results = statement.executeBatch(); // Thực thi batch

            // Kiểm tra tất cả kết quả, nếu có lỗi thì return false
            for (int result : results) {
                if (result < 0) {
                    return false; // Một câu lệnh INSERT bị lỗi
                }
            }

            return true; // Nếu tất cả đều thành công

        } catch (SQLException e) {
            System.err.println("Error inserting detail invoice: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteDetailInvoiceByInvoiceId(int invoiceId) {
        final String query = "DELETE FROM detail_invoice WHERE invoice_id = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, invoiceId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting from " + table + ": " + e.getMessage());
            return false;
        }
    }

}
