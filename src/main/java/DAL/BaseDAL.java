package DAL;

import INTERFACE.IDAL;
import INTERFACE.ConnectionFactory;
import java.sql.*;
import java.util.ArrayList;

public abstract class BaseDAL<T, K> implements IDAL<T, K> {
    protected final ConnectionFactory connectionFactory;
    protected final String table;
    protected final String idColumn;

    protected BaseDAL(ConnectionFactory connectionFactory, String table, String idColumn) {
        this.connectionFactory = connectionFactory;
        this.table = table;
        this.idColumn = idColumn;
    }

    @Override
    public ArrayList<T> getAll() {
        final String query = "SELECT * FROM " + table;
        ArrayList<T> list = new ArrayList<>();

        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                list.add(mapResultSetToObject(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving " + table + ": " + e.getMessage());
        }
        return list;
    }

    @Override
    public T getById(K id) {
        final String query = "SELECT * FROM " + table + " WHERE " + idColumn + " = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (id instanceof Integer) {
                statement.setInt(1, (Integer) id);
            } else if (id instanceof String) {
                statement.setString(1, (String) id);
            } else {
                throw new IllegalArgumentException("Unsupported ID type: " + id.getClass().getSimpleName());
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToObject(resultSet);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving " + table + " by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public abstract boolean insert(T obj);

    @Override
    public boolean update(T obj) {
        final String query = "UPDATE " + table + " " + getUpdateQuery();
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            setUpdateParameters(statement, obj);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating " + table + ": " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(K id) {
        final String query = "DELETE FROM " + table + " WHERE " + idColumn + " = ?";
        try (Connection connection = connectionFactory.newConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            if (id instanceof Integer) {
                statement.setInt(1, (Integer) id);
            } else if (id instanceof String) {
                statement.setString(1, (String) id);
            } else {
                throw new IllegalArgumentException("Unsupported ID type: " + id.getClass().getSimpleName());
            }

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting from " + table + ": " + e.getMessage());
            return false;
        }
    }

    protected abstract T mapResultSetToObject(ResultSet resultSet) throws SQLException;

    protected String getUpdateQuery() {
        throw new UnsupportedOperationException("Update operation not supported.");
    }

    protected void setInsertParameters(PreparedStatement statement, T obj) throws SQLException {
        throw new UnsupportedOperationException("Insert operation not supported.");
    }

    protected void setUpdateParameters(PreparedStatement statement, T obj) throws SQLException {
        throw new UnsupportedOperationException("Update operation not supported.");
    }

}
