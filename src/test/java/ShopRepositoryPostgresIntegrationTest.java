
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer; // Zmieniono na PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date; // Nadal używamy java.sql.Date
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers // Adnotacja integrująca Testcontainers z JUnit 5
class ShopRepositoryPostgresIntegrationTest {

    private ShopRepository shopRepository;

    // Definiujemy kontener PostgreSQL. Testcontainers automatycznie zarządza jego cyklem życia.
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13.3")) // Zmieniono obraz na postgres:13.3
            .withDatabaseName("test_sklep_db_pg") // Nazwa bazy danych w kontenerze
            .withUsername("testuser_pg")        // Nazwa użytkownika
            .withPassword("testpass_pg");       // Hasło

    @BeforeEach
    void setUp() throws SQLException {
        // Tworzymy ActualConnectionProvider, który będzie łączył się z uruchomionym kontenerem PostgreSQL
        ConnectionProvider connectionProvider = new ActualConnectionProvider(
                postgresContainer.getJdbcUrl(), // Używamy getJdbcUrl z postgresContainer
                postgresContainer.getUsername(),
                postgresContainer.getPassword()
        );
        shopRepository = new ShopRepository(connectionProvider);

        // Inicjalizacja schematu bazy danych i wstawienie danych testowych
        // UWAGA: Składnia SQL dostosowana do PostgreSQL
        try (Connection conn = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
             Statement stmt = conn.createStatement()) {

            // Usuwamy tabelę, jeśli istnieje, aby zapewnić czysty stan przed każdym testem
            stmt.execute("DROP TABLE IF EXISTS towary");
            // Tworzymy tabelę - używamy SERIAL dla auto-inkrementacji, DOUBLE PRECISION dla liczb zmiennoprzecinkowych
            stmt.execute("CREATE TABLE towary (" +
                    "id_towaru SERIAL PRIMARY KEY," + // SERIAL PRIMARY KEY dla auto-inkrementacji w PG
                    "nazwa VARCHAR(255) NOT NULL," +
                    "opis VARCHAR(255)," +
                    "cena_jednostkowa DOUBLE PRECISION NOT NULL," + // DOUBLE PRECISION w PG
                    "ilosc_dostepna INT NOT NULL," +
                    "data_dodania DATE NOT NULL)");

            // Wstawiamy przykładowe dane - id_towaru nie podajemy, bo SERIAL automatycznie je generuje
            stmt.execute("INSERT INTO towary (nazwa, opis, cena_jednostkowa, ilosc_dostepna, data_dodania) VALUES " +
                    "('Monitor PG', 'Monitor gamingowy LED PG', 1500.0, 5, '2024-01-01'), " +
                    "('Klawiatura PG', 'Mechaniczna klawiatura RGB PG', 300.0, 20, '2024-01-10')");
        }
    }

    @Test
    void shouldReturnTowaryFromPostgresDatabase() throws SQLException {
        List<Towar> towary = shopRepository.getTowary();

        assertEquals(2, towary.size()); // Oczekujemy dwóch towarów

        // UWAGA: ID towarów w PostgreSQL z SERIAL zaczynają się od 1 i są generowane.
        // Jeśli nie pobierasz ID z bazy (lub nie resetujesz sekwencji), musisz być ostrożny z asercjami ID.
        // W tym przypadku, skoro pobierasz je z bazy i wstawiasz 2 rekordy, będą to prawdopodobnie 1 i 2.
        // Jeśli orderowanie nie jest gwarantowane, możesz posortować listę lub szukać obiektów.
        // Dla uproszczenia zakładamy tu, że kolejność pobierania jest taka sama jak wstawiania.

        // Ponieważ ID jest generowane przez SERIAL, lepiej pobrać je z rzeczywistego obiektu Towar
        // i porównać resztę pól. Lub sprawdzić, czy lista zawiera oczekiwane obiekty (bez ID, jeśli ID nie jest kluczowe dla testu)
        // Jeśli ID jest krytyczne, musisz wykonać zapytanie SELECT z ORDER BY, aby zagwarantować kolejność.

        // Pobieramy pierwszy towar i sprawdzamy jego pola
        Towar actualTowar1 = towary.get(0);
        assertEquals("Monitor PG", actualTowar1.getNazwa());
        assertEquals("Monitor gamingowy LED PG", actualTowar1.getOpis());
        assertEquals(1500.0, actualTowar1.getCenaJednostkowa());
        assertEquals(5, actualTowar1.getIloscDostepna());
        assertEquals(Date.valueOf("2024-01-01"), actualTowar1.getDataDodania());

        // Pobieramy drugi towar i sprawdzamy jego pola
        Towar actualTowar2 = towary.get(1);
        assertEquals("Klawiatura PG", actualTowar2.getNazwa());
        assertEquals("Mechaniczna klawiatura RGB PG", actualTowar2.getOpis());
        assertEquals(300.0, actualTowar2.getCenaJednostkowa());
        assertEquals(20, actualTowar2.getIloscDostepna());
        assertEquals(Date.valueOf("2024-01-10"), actualTowar2.getDataDodania());
    }

    @Test
    void shouldReturnEmptyListWhenNoTowaryInPostgres() throws SQLException {
        // Usuwamy wszystkie dane, aby sprawdzić scenariusz pustej tabeli
        try (Connection conn = DriverManager.getConnection(
                postgresContainer.getJdbcUrl(),
                postgresContainer.getUsername(),
                postgresContainer.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM towary");
        }

        List<Towar> towary = shopRepository.getTowary();
        assertTrue(towary.isEmpty()); // Oczekujemy pustej listy
    }
}
