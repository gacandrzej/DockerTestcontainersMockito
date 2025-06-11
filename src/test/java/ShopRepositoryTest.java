
import gac.andrzej.sklep.ConnectionProvider;
import gac.andrzej.sklep.ShopRepository;
import gac.andrzej.sklep.Towar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ShopRepositoryTest {

    private ShopRepository shopRepository;

    @Mock
    private ConnectionProvider mockConnectionProvider; // Mockujemy dostawcę połączeń
    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        // Inicjalizuje mocki z adnotacjami @Mock
        MockitoAnnotations.openMocks(this);
        // Tworzymy ShopRepository z mockowym dostawcą połączeń
        shopRepository = new ShopRepository(mockConnectionProvider);

        // Definiujemy zachowanie mockConnectionProvider: zawsze zwracaj mockConnection
        when(mockConnectionProvider.getConnection()).thenReturn(mockConnection);
        // Definiujemy zachowanie mockConnection: createStatement zawsze zwracaj mockStatement
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        // Definiujemy zachowanie mockStatement: executeQuery zawsze zwracaj mockResultSet
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
    }

    @Test
    void shouldReturnTowaryWhenDataExists() throws SQLException {
        // Symulujemy dane, które zwróci ResultSet
        when(mockResultSet.next())
                .thenReturn(true) // Pierwszy wiersz
                .thenReturn(true) // Drugi wiersz
                .thenReturn(false); // Koniec danych

        // Dane dla pierwszego wiersza
        when(mockResultSet.getInt("id_towaru")).thenReturn(1);
        when(mockResultSet.getString("nazwa")).thenReturn("Laptop");
        when(mockResultSet.getString("opis")).thenReturn("Gamingowy laptop");
        when(mockResultSet.getDouble("cena_jednostkowa")).thenReturn(4500.0);
        when(mockResultSet.getInt("ilosc_dostepna")).thenReturn(10);
        when(mockResultSet.getDate("data_dodania")).thenReturn(Date.valueOf("2023-01-15"));

        // Dane dla drugiego wiersza (musimy użyć when().thenReturn() kolejny raz dla tych samych metod)
        // Mockito pamięta wywołania i zwraca kolejne zdefiniowane wartości
        when(mockResultSet.getInt("id_towaru")).thenReturn(2);
        when(mockResultSet.getString("nazwa")).thenReturn("Mysz");
        when(mockResultSet.getString("opis")).thenReturn("Bezprzewodowa mysz");
        when(mockResultSet.getDouble("cena_jednostkowa")).thenReturn(150.0);
        when(mockResultSet.getInt("ilosc_dostepna")).thenReturn(50);
        when(mockResultSet.getDate("data_dodania")).thenReturn(Date.valueOf("2023-02-01"));


        List<Towar> towary = shopRepository.getTowary();

        // Weryfikujemy, czy lista ma oczekiwany rozmiar
        assertEquals(2, towary.size());

        // Weryfikujemy dane pierwszego towaru
        Towar expectedTowar1 = new Towar(1, "Laptop", "Gamingowy laptop", 4500.0, 10, Date.valueOf("2023-01-15"));
        assertEquals(expectedTowar1, towary.get(0));

        // Weryfikujemy dane drugiego towaru
        Towar expectedTowar2 = new Towar(2, "Mysz", "Bezprzewodowa mysz", 150.0, 50, Date.valueOf("2023-02-01"));
        assertEquals(expectedTowar2, towary.get(1));

        // Weryfikujemy, czy metody na mockach zostały wywołane poprawnie
        verify(mockConnectionProvider).getConnection();
        verify(mockConnection).createStatement();
        verify(mockStatement).executeQuery("SELECT id_towaru, nazwa, opis, cena_jednostkowa, ilosc_dostepna, data_dodania FROM towary");
        verify(mockResultSet, times(3)).next(); // Dwa razy true, raz false
        verify(mockResultSet, times(2)).getInt("id_towaru"); // Po dwa razy dla każdego pola
        verify(mockResultSet, times(2)).getString("nazwa");
        // ... i tak dalej dla wszystkich pól
    }

    @Test
    void shouldReturnEmptyListWhenNoData() throws SQLException {
        // Symulujemy, że ResultSet nie ma żadnych danych
        when(mockResultSet.next()).thenReturn(false);

        List<Towar> towary = shopRepository.getTowary();

        assertEquals(0, towary.size());
        // Weryfikujemy, że metoda next() została wywołana tylko raz (żeby sprawdzić, czy są dane)
        verify(mockResultSet, times(1)).next();
    }

    @Test
    void shouldThrowSQLExceptionWhenConnectionProviderFails() throws SQLException {
        // Symulujemy, że ConnectionProvider rzuca SQLException
        when(mockConnectionProvider.getConnection()).thenThrow(new SQLException("Błąd połączenia z bazą danych"));

        // Oczekujemy, że metoda getTowary() rzuci SQLException
        assertThrows(SQLException.class, () -> shopRepository.getTowary());

        // Weryfikujemy, że próbowano uzyskać połączenie
        verify(mockConnectionProvider).getConnection();
        // Upewniamy się, że nie było dalszych interakcji z Connection, Statement czy ResultSet
        verifyNoMoreInteractions(mockConnection, mockStatement, mockResultSet);
    }
}