package ao.co.atlantico.candidaturas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Centraliza a configuracao e a abertura de conexoes PostgreSQL. */
public final class ConnectionFactory {

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/cadastro_atlantico";
    private static final String DEFAULT_USER = "cadastro_app";
    private static final String DEFAULT_PASSWORD = "cadastro123";

    private final String url;
    private final String user;
    private final String password;

    static {
        try {
            // O carregamento explicito evita falhas de descoberta do driver em alguns Tomcat empacotados.
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException exception) {
            throw new ExceptionInInitializerError("Driver JDBC do PostgreSQL nao encontrado no WAR.");
        }
    }

    public ConnectionFactory() {
        this.url = configuration("DB_URL", "db.url", DEFAULT_URL);
        this.user = configuration("DB_USER", "db.user", DEFAULT_USER);
        this.password = configuration("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);
    }

    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        connection.setReadOnly(false);
        return connection;
    }

    private static String configuration(String environmentName, String propertyName, String defaultValue) {
        String value = System.getenv(environmentName);
        if (value == null || value.isBlank()) {
            value = System.getProperty(propertyName);
        }
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }
}
