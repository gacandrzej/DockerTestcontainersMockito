package gac.andrzej.sklep;

import java.sql.Connection;
import java.sql.SQLException;

// Interfejs do dostarczania połączeń z bazą danych
public interface ConnectionProvider {
    Connection getConnection() throws SQLException;
}
