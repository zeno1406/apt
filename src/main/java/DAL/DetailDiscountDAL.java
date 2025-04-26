package DAL;

import DTO.DetailDiscountDTO;
import DTO.DetailImportDTO;
import DTO.RolePermissionDTO;

import java.sql.*;
import java.util.ArrayList;

/*
+ Remain getAll
+ Remain getById
+ Override insert - Not Need GENERATED_KEYS
+ Remain update - Update total_price_invoice and discount_amount
+ Remain delete (hard delete)
+ Need Insert ArrayList
*/

public class DetailDiscountDAL extends BaseDAL<DetailDiscountDTO, String> {
    public static final DetailDiscountDAL INSTANCE = new DetailDiscountDAL();

    private DetailDiscountDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "detail_discount", "discount_code");
    }

    public static DetailDiscountDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DetailDiscountDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DetailDiscountDTO(
                resultSet.getString("discount_code"),
                resultSet.getBigDecimal("total_price_invoice"),
                resultSet.getBigDecimal("discount_amount")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(discount_code, total_price_invoice, discount_amount) VALUES (?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DetailDiscountDTO obj) throws SQLException {
        statement.setString(1, obj.getDiscountCode());
        statement.setBigDecimal(2, obj.getTotalPriceInvoice());
        statement.setBigDecimal(3, obj.getDiscountAmount());
    }

    @Override
    protected String getUpdateQuery() {
        return "SET total_price_invoice = ?, discount_amount = ? WHERE discount_code = ?";
    }

    @Override
    protected void setUpdateParameters(PreparedStatement statement, DetailDiscountDTO obj) throws SQLException {
        statement.setBigDecimal(1, obj.getTotalPriceInvoice());
        statement.setBigDecimal(2, obj.getDiscountAmount());
        statement.setString(3, obj.getDiscountCode());
    }
    public boolean insertAllDetailDiscountByDiscountCode(String discountCode, ArrayList<DetailDiscountDTO> list) {
        final String query = "INSERT INTO detail_discount (discount_code, total_price_invoice, discount_amount) VALUES (?, ?, ?)";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (DetailDiscountDTO obj : list) {
                statement.setString(1, discountCode);
                statement.setBigDecimal(2, obj.getTotalPriceInvoice());
                statement.setBigDecimal(3, obj.getDiscountAmount());
                statement.addBatch();
            }

            int[] results = statement.executeBatch();

            for (int result : results) {
                if (result < 0) {
                    return false;
                }
            }

            return true;

        } catch (SQLException e) {
            System.err.println("Error inserting detail discount: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAllDetailDiscountByDiscountCode(String discountCode) {
        final String query = "DELETE FROM detail_discount WHERE discount_code = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, discountCode);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting from " + table + ": " + e.getMessage());
            return false;
        }
    }

    public ArrayList<DetailDiscountDTO> getAllDetailDiscountByDiscountCode(String discountCode) {
        final String query = "SELECT * FROM detail_discount WHERE discount_code = ?";
        ArrayList<DetailDiscountDTO> list = new ArrayList<>();

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, discountCode);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    list.add(mapResultSetToObject(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving " + table + ": " + e.getMessage());
        }
        return list;
    }

}
