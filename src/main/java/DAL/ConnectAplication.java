package DAL;

import INTERFACE.ConnectionFactory;

public class ConnectAplication {
    private final ConnectionFactory connectionFactory;
    private static final ConnectAplication INSTANCE = new ConnectAplication();

    private ConnectAplication() {
        connectionFactory = new ConnectionFactoryImpl();
    }

    public static ConnectAplication getInstance() {
        return INSTANCE;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
