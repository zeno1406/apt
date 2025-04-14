package FACTORY;

import INTERFACE.ConnectionFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionFactoryImpl implements ConnectionFactory {
    private static final String URL = "jdbc:mysql://localhost:3306/java_sql?useAffectedRows=true&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
    public ConnectionFactoryImpl() {
        testConnection();
    }

    @Override
    public Connection newConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Override
    public boolean isConnected(Connection connection) {
        return connection != null;
    }

    private void testConnection() {
        try (Connection connection = newConnection()) {
            System.out.println("Database connected successfully!");
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}
