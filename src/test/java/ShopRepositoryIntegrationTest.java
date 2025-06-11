
import gac.andrzej.sklep.ActualConnectionProvider;
import gac.andrzej.sklep.ConnectionProvider;
import gac.andrzej.sklep.ShopRepository;
import gac.andrzej.sklep.Towar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers // Adnotacja integrująca Testcontainers z JUnit 5
class ShopRepositoryIntegrationTest {

    private ShopRepository shopRepository;

    // Definiujemy kontener MySQL. Testcontainers automatycznie zarządza jego cyklem życia.
    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("test_sklep_db") // Nazwa bazy danych w kontenerze
            .withUsername("testuser")        // Nazwa użytkownika
            .withPassword("testpass");       // Hasło

    @BeforeEach
    void setUp() throws SQLException {
        // Tworzymy ActualConnectionProvider, który będzie łączył się z uruchomionym kontenerem
        ConnectionProvider connectionProvider = new ActualConnectionProvider(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );
        shopRepository = new ShopRepository(connectionProvider);

        // Inicjalizacja schematu bazy danych i wstawienie danych testowych
        try (Connection conn = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword());
             Statement stmt = conn.createStatement()) {

            // Usuwamy tabelę, jeśli istnieje, aby zapewnić czysty stan przed każdym testem
            stmt.execute("DROP TABLE IF EXISTS towary");
            // Tworzymy tabelę
            stmt.execute("CREATE TABLE towary (" +
                    "id_towaru INT PRIMARY KEY," +
                    "nazwa VARCHAR(255) NOT NULL," +
                    "opis VARCHAR(255)," +
                    "cena_jednostkowa DOUBLE NOT NULL," +
                    "ilosc_dostepna INT NOT NULL," +
                    "data_dodania DATE NOT NULL)");

            // Wstawiamy przykładowe dane
            stmt.execute("INSERT INTO towary (id_towaru, nazwa, opis, cena_jednostkowa, ilosc_dostepna, data_dodania) VALUES " +
                    "(1, 'Monitor', 'Monitor gamingowy LED', 1500.0, 5, '2024-01-01'), " +
                    "(2, 'Klawiatura', 'Mechaniczna klawiatura RGB', 300.0, 20, '2024-01-10')");
        }
    }

    @Test
    void shouldReturnTowaryFromRealDatabase() throws SQLException {
        List<Towar> towary = shopRepository.getTowary();

        assertEquals(2, towary.size()); // Oczekujemy dwóch towarów

        // Weryfikujemy pierwszy towar
        Towar expectedTowar1 = new Towar(1, "Monitor", "Monitor gamingowy LED", 1500.0, 5, Date.valueOf("2024-01-01"));
        assertEquals(expectedTowar1, towary.get(0));

        // Weryfikujemy drugi towar
        Towar expectedTowar2 = new Towar(2, "Klawiatura", "Mechaniczna klawiatura RGB", 300.0, 20, Date.valueOf("2024-01-10"));
        assertEquals(expectedTowar2, towary.get(1));
    }

    @Test
    void shouldReturnEmptyListWhenNoTowary() throws SQLException {
        // Usuwamy wszystkie dane, aby sprawdzić scenariusz pustej tabeli
        try (Connection conn = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM towary");
        }

        List<Towar> towary = shopRepository.getTowary();
        assertTrue(towary.isEmpty()); // Oczekujemy pustej listy
    }
}