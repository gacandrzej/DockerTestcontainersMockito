package gac.andrzej.sklep;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Klasa odpowiedzialna za operacje na bazie danych
public class ShopRepository {
    private final ConnectionProvider connectionProvider;

    // Wstrzykujemy ConnectionProvider przez konstruktor
    public ShopRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public List<Towar> getTowary() throws SQLException {
        List<Towar> result = new ArrayList<>();
        String sql = "SELECT id_towaru, nazwa, opis, cena_jednostkowa, ilosc_dostepna, data_dodania FROM towary";

        // Używamy połączenia dostarczonego przez ConnectionProvider
        try (Connection conn = connectionProvider.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Towar towar = new Towar(
                        rs.getInt("id_towaru"),
                        rs.getString("nazwa"),
                        rs.getString("opis"),
                        rs.getDouble("cena_jednostkowa"),
                        rs.getInt("ilosc_dostepna"),
                        rs.getDate("data_dodania")
                );
                result.add(towar);
            }
        }
        return result;
    }
}
