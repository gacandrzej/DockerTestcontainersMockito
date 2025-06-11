
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

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("test_sklep_db")
            .withUsername("testuser")
            .withPassword("testpass");

    @BeforeEach
    void setUp() throws SQLException {
        // Tworzymy ActualConnectionProvider, który będzie łączył się z uruchomionym kontenerem MySQL
        ConnectionProvider connectionProvider = new ActualConnectionProvider(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword()
        );
        // Tworzymy ShopRepository z PRAWDZIWYM dostawcą połączeń
        shopRepository = new ShopRepository(connectionProvider);

        // Inicjalizacja schematu bazy danych i wstawienie danych testowych
        try (Connection conn = DriverManager.getConnection(
                mysqlContainer.getJdbcUrl(),
                mysqlContainer.getUsername(),
                mysqlContainer.getPassword());
             Statement stmt = conn.createStatement()) {

            stmt.execute("DROP TABLE IF EXISTS towary");
            stmt.execute("CREATE TABLE towary (" +
                    "id_towaru INT PRIMARY KEY," +
                    "nazwa VARCHAR(255) NOT NULL," +
                    "opis VARCHAR(255)," +
                    "cena_jednostkowa DOUBLE NOT NULL," +
                    "ilosc_dostepna INT NOT NULL," +
                    "data_dodania DATE NOT NULL)");

            stmt.execute("INSERT INTO towary (id_towaru, nazwa, opis, cena_jednostkowa, ilosc_dostepna, data_dodania) VALUES " +
                    "(1, 'Monitor', 'Monitor gamingowy LED', 1500.0, 5, '2024-01-01'), " +
                    "(2, 'Klawiatura', 'Mechaniczna klawiatura RGB', 300.0, 20, '2024-01-10')");
        }
    }

    @Test
    void shouldReturnTowaryFromRealDatabase() throws SQLException {
        // Wywołujemy metodę getTowary() bez argumentów, tak jak jest zaimplementowana w ShopRepository
        List<Towar> towary = shopRepository.getTowary();

        assertEquals(2, towary.size());

        Towar expectedTowar1 = new Towar(1, "Monitor", "Monitor gamingowy LED", 1500.0, 5, Date.valueOf("2024-01-01"));
        assertEquals(expectedTowar1, towary.get(0));

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

        List<Towar> towary = shopRepository.getTowary(); // Wywołujemy metodę bez argumentów
        assertTrue(towary.isEmpty());
    }
}
