package DAL;

import FACTORY.ConnectionFactoryImpl;
import INTERFACE.ConnectionFactory;

public class ConnectApplication {
    private final ConnectionFactory connectionFactory;
    private static final ConnectApplication INSTANCE = new ConnectApplication();

    private ConnectApplication() {
        connectionFactory = new ConnectionFactoryImpl();
    }

    public static ConnectApplication getInstance() {
        return INSTANCE;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }
}
