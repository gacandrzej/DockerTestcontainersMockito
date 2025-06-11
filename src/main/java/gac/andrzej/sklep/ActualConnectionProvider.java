package gac.andrzej.sklep;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Implementacja ConnectionProvider dla rzeczywistych połączeń JDBC
public class ActualConnectionProvider implements ConnectionProvider {
    private final String dbUrl;
    private final String user;
    private final String pass;

    public ActualConnectionProvider(String dbUrl, String user, String pass) {
        this.dbUrl = dbUrl;
        this.user = user;
        this.pass = pass;
    }

    @Override
    public Connection getConnection() throws SQLException {
        // DriverManager.getConnection jest bezpieczne w try-with-resources
        // Połączenie będzie zarządzane przez wywołującego (np. ShopRepository)
        return DriverManager.getConnection(dbUrl, user, pass);
    }
}
