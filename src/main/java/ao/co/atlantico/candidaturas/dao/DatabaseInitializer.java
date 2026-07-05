package ao.co.atlantico.candidaturas.dao;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class DatabaseInitializer {

    private final ConnectionFactory connectionFactory;

    public DatabaseInitializer(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void initialize() throws SQLException, IOException {
        String schema = readSchema();
        try (Connection connection = connectionFactory.getConnection();
             Statement statement = connection.createStatement()) {
            for (String command : schema.split(";")) {
                if (!command.isBlank()) {
                    statement.execute(command);
                }
            }
        }
    }

    private String readSchema() throws IOException {
        try (InputStream stream = DatabaseInitializer.class.getResourceAsStream("/db/schema.sql")) {
            if (stream == null) {
                throw new IOException("O recurso db/schema.sql nao foi encontrado.");
            }
            String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return Arrays.stream(content.split("\\R"))
                .filter(line -> !line.trim().startsWith("--"))
                .collect(Collectors.joining(System.lineSeparator()));
        }
    }
}
