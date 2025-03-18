package INTERFACE;
import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionFactory {
    Connection newConnection() throws SQLException;

    void closeConnection(Connection connection) throws SQLException;

    boolean isConnected(Connection connection);
}
