package gac.andrzej.sklep;

import java.sql.SQLException;
import java.util.List;

public class ShopApplication {

    public static void main(String[] args) {
        // Pobieramy dane do połączenia z bazy danych ze zmiennych środowiskowych
        // To jest bezpieczniejsze i bardziej elastyczne
        final String DB_URL = System.getenv("DB_URL");
        final String USER = System.getenv("DB_USER");
        final String PASS = System.getenv("DB_PASS");

        if (DB_URL == null || USER == null || PASS == null) {
            System.err.println("Brak wymaganych zmiennych środowiskowych: DB_URL, DB_USER, DB_PASS");
            System.exit(1); // Zakończ aplikację z błędem
        }

        // Tworzymy rzeczywistego dostawcę połączeń
        ConnectionProvider connectionProvider = new ActualConnectionProvider(DB_URL, USER, PASS);
        // Tworzymy instancję ShopRepository z dostawcą połączeń
        ShopRepository shopRepository = new ShopRepository(connectionProvider);

        try {
            System.out.println("Pobieranie towarów z bazy danych...");
            List<Towar> towary = shopRepository.getTowary();
            if (towary.isEmpty()) {
                System.out.println("Brak towarów w bazie danych.");
            } else {
                towary.forEach(System.out::println);
            }
        } catch (SQLException e) {
            System.err.println("Wystąpił błąd podczas pobierania towarów: " + e.getMessage());
            e.printStackTrace(); // Wyświetl pełny stos wywołań dla debugowania
        }
    }
}