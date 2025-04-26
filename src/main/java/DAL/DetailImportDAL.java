package DAL;

import DTO.DetailImportDTO;

import java.sql.*;
import java.util.ArrayList;

/*
+ Remain getAll
+ Remain getById
+ Override insert - Not Need GENERATED_KEYS
+ Override update - Not allowed
+ Override delete - Hard delete (allowed)
+ Need Insert ArrayList
*/

public class DetailImportDAL extends BaseDAL<DetailImportDTO, Integer> {
    public static final DetailImportDAL INSTANCE = new DetailImportDAL();

    private DetailImportDAL() {
        super(ConnectApplication.getInstance().getConnectionFactory(), "detail_import", "import_id");
    }

    public static DetailImportDAL getInstance() {
        return INSTANCE;
    }

    @Override
    protected DetailImportDTO mapResultSetToObject(ResultSet resultSet) throws SQLException {
        return new DetailImportDTO(
                resultSet.getInt("import_id"),
                resultSet.getString("product_id"),
                resultSet.getInt("quantity"),
                resultSet.getBigDecimal("price"),
                resultSet.getBigDecimal("total_price")
        );
    }

    @Override
    protected String getInsertQuery() {
        return "(import_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";
    }

    @Override
    protected void setInsertParameters(PreparedStatement statement, DetailImportDTO obj) throws SQLException {
        statement.setInt(1, obj.getImportId());
        statement.setString(2, obj.getProductId());
        statement.setInt(3, obj.getQuantity());
        statement.setBigDecimal(4, obj.getPrice());
        statement.setBigDecimal(5, obj.getTotalPrice());
    }

    @Override
    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Cannot update permission records.");
    }

    public boolean insertAllDetailImportByImportId(int importId, ArrayList<DetailImportDTO> list) {
        final String query = "INSERT INTO detail_import (import_id, product_id, quantity, price, total_price) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            for (DetailImportDTO detail : list) {
                statement.setInt(1, importId);
                statement.setString(2, detail.getProductId());
                statement.setInt(3, detail.getQuantity());
                statement.setBigDecimal(4, detail.getPrice());
                statement.setBigDecimal(5, detail.getTotalPrice());
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
            System.err.println("Error inserting detail import: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAllDetailImportByImportId(int importId) {
        final String query = "DELETE FROM detail_import WHERE import_id = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, importId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting from " + table + ": " + e.getMessage());
            return false;
        }
    }

    public ArrayList<DetailImportDTO> getAllDetailImportByImportId(int importId) {
        final String query = "SELECT * FROM detail_import WHERE import_id = ?";
        ArrayList<DetailImportDTO> list = new ArrayList<>();

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, importId);
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
