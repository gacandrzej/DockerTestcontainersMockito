import gac.andrzej.sklep.ActualConnectionProvider;
import gac.andrzej.sklep.ConnectionProvider;
import gac.andrzej.sklep.ShopRepository;
import gac.andrzej.sklep.Towar;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShopRepositorySqliteIntegrationTest {

    private ShopRepository shopRepository;
    private File sqliteDbFile; // Referencja do tymczasowego pliku bazy danych SQLite
    private String jdbcUrl;

    @BeforeEach
    void setUp() throws IOException, SQLException {
        // Tworzymy tymczasowy plik dla bazy danych SQLite
        // Files.createTempFile tworzy unikalny plik, a ".db" to rozszerzenie
        sqliteDbFile = Files.createTempFile("test_sklep_sqlite", ".db").toFile();
        sqliteDbFile.deleteOnExit(); // Upewniamy się, że plik zostanie usunięty po wyjściu z JVM

        // Format URL JDBC dla SQLite: jdbc:sqlite:ścieżka_do_pliku
        jdbcUrl = "jdbc:sqlite:" + sqliteDbFile.getAbsolutePath();

        // Tworzymy ConnectionProvider dla SQLite
        ConnectionProvider connectionProvider = new ActualConnectionProvider(jdbcUrl, "", ""); // SQLite nie używa użytkownika/hasła
        shopRepository = new ShopRepository(connectionProvider);

        // Inicjalizacja schematu bazy danych i wstawienie danych testowych
        // UWAGA: Składnia SQL dostosowana do SQLite
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {

            // Usuwamy tabelę, jeśli istnieje (chociaż dla temp file nie jest to konieczne, ale dobra praktyka)
            stmt.execute("DROP TABLE IF EXISTS towary");
            // Tworzymy tabelę - INTEGER PRIMARY KEY AUTOINCREMENT dla auto-inkrementacji w SQLite
            // REAL dla liczb zmiennoprzecinkowych, INT dla całkowitych, DATE dla dat
            stmt.execute("CREATE TABLE towary (" +
                    "id_towaru INTEGER PRIMARY KEY AUTOINCREMENT," + // AUTOINCREMENT w SQLite
                    "nazwa TEXT NOT NULL," + // TEXT zamiast VARCHAR(255) dla SQLite
                    "opis TEXT," +
                    "cena_jednostkowa REAL NOT NULL," + // REAL dla liczb zmiennoprzecinkowych
                    "ilosc_dostepna INTEGER NOT NULL," +
                    "data_dodania DATE NOT NULL)");

            // Wstawiamy przykładowe dane - id_towaru nie podajemy, bo AUTOINCREMENT automatycznie je generuje
            stmt.execute("INSERT INTO towary (nazwa, opis, cena_jednostkowa, ilosc_dostepna, data_dodania) VALUES " +
                    "('Książka SQLite', 'Fantastyka naukowa', 50.0, 100, '2024-03-01'), " +
                    "('Długopis SQLite', 'Długopis żelowy', 5.0, 500, '2024-03-05')");
        }
    }

    @AfterEach
    void tearDown() {
        // Upewniamy się, że połączenia są zamknięte, aby plik mógł być usunięty
        // DriverManager.getConnection() zarządza wewnętrznie pulą, więc nie ma bezpośredniej metody zamknięcia dla repozytorium.
        // Jeśli repozytorium miałoby metodę close() dla połączenia, należałoby ją wywołać.
        // Dla SQLite opartego na plikach, usunięcie pliku jest kluczowe.
        if (sqliteDbFile != null && sqliteDbFile.exists()) {
            // Próba usunięcia pliku. Jeśli połączenia są nadal otwarte, może się nie udać.
            // W środowisku testowym JUnit/Gradle często pomaga ponowne uruchomienie JVM.
            // deleteOnExit() zapewnia to w ostateczności.
            if (!sqliteDbFile.delete()) {
                System.err.println("Could not delete SQLite database file: " + sqliteDbFile.getAbsolutePath());
            }
        }
    }

    @Test
    void shouldReturnTowaryFromSqliteDatabase() throws SQLException {
        List<Towar> towary = shopRepository.getTowary();

        assertEquals(2, towary.size());

        // Ponieważ ID jest generowane przez AUTOINCREMENT, lepiej porównać resztę pól
        // lub sprawdzić, czy lista zawiera oczekiwane obiekty bez uwzględniania ID.
        Towar actualTowar1 = towary.get(0);
        assertEquals("Książka SQLite", actualTowar1.getNazwa());
        assertEquals(50.0, actualTowar1.getCenaJednostkowa());
        assertEquals(100, actualTowar1.getIloscDostepna());
        assertEquals(LocalDate.of(2024, 3, 1), actualTowar1.getDataDodania().toLocalDate());

        Towar actualTowar2 = towary.get(1);
        assertEquals("Długopis SQLite", actualTowar2.getNazwa());
        assertEquals(5.0, actualTowar2.getCenaJednostkowa());
        assertEquals(500, actualTowar2.getIloscDostepna());
        assertEquals(LocalDate.of(2024, 3, 5), actualTowar2.getDataDodania().toLocalDate());
    }

    @Test
    void shouldReturnEmptyListWhenNoTowaryInSqlite() throws SQLException {
        try (Connection conn = DriverManager.getConnection(jdbcUrl);
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM towary");
        }

        List<Towar> towary = shopRepository.getTowary();
        assertTrue(towary.isEmpty());
    }
}
