
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

    // Mockujemy dostawcę połączeń oraz obiekty JDBC
    @Mock
    private ConnectionProvider mockConnectionProvider;
    @Mock
    private Connection mockConnection;
    @Mock
    private Statement mockStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        // Inicjalizuje wszystkie mocki oznaczone adnotacją @Mock
        MockitoAnnotations.openMocks(this);
        // Tworzymy ShopRepository Z MOCKOWYM DOSTAWCĄ POŁĄCZEŃ
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
        // Symulujemy, że ResultSet ma dwa wiersze, a potem kończy się
        when(mockResultSet.next())
                .thenReturn(true) // Wiersz 1
                .thenReturn(true) // Wiersz 2
                .thenReturn(false); // Koniec danych

        // Łańcuchowanie zwracanych wartości dla każdej metody ResultSet
        // Mockito zwróci pierwszą wartość przy pierwszym wywołaniu, drugą przy drugim itd.
        when(mockResultSet.getInt("id_towaru"))
                .thenReturn(1) // Wiersz 1: id_towaru
                .thenReturn(2); // Wiersz 2: id_towaru

        when(mockResultSet.getString("nazwa"))
                .thenReturn("Laptop") // Wiersz 1: nazwa
                .thenReturn("Mysz");   // Wiersz 2: nazwa

        when(mockResultSet.getString("opis"))
                .thenReturn("Gamingowy laptop") // Wiersz 1: opis
                .thenReturn("Bezprzewodowa mysz"); // Wiersz 2: opis

        when(mockResultSet.getDouble("cena_jednostkowa"))
                .thenReturn(4500.0) // Wiersz 1: cena_jednostkowa
                .thenReturn(150.0);   // Wiersz 2: cena_jednostkowa

        when(mockResultSet.getInt("ilosc_dostepna"))
                .thenReturn(10) // Wiersz 1: ilosc_dostepna
                .thenReturn(50);  // Wiersz 2: ilosc_dostepna

        when(mockResultSet.getDate("data_dodania"))
                .thenReturn(Date.valueOf("2023-01-15")) // Wiersz 1: data_dodania
                .thenReturn(Date.valueOf("2023-02-01"));  // Wiersz 2: data_dodania


        // WYWOŁUJEMY testowaną metodę getTowary() BEZ argumentów, tak jak jest zaimplementowana w ShopRepository
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
        // Metoda getConnection() na mockConnectionProvider powinna być wywołana raz
        verify(mockConnectionProvider).getConnection();
        // Dalej weryfikujemy interakcje z mockami JDBC, które pochodzą z mockConnectionProvider
        verify(mockConnection).createStatement();
        verify(mockStatement).executeQuery("SELECT id_towaru, nazwa, opis, cena_jednostkowa, ilosc_dostepna, data_dodania FROM towary");
        verify(mockResultSet, times(3)).next();
        verify(mockResultSet, times(2)).getInt("id_towaru");
        verify(mockResultSet, times(2)).getString("nazwa");
        verify(mockResultSet, times(2)).getString("opis");
        verify(mockResultSet, times(2)).getDouble("cena_jednostkowa");
        verify(mockResultSet, times(2)).getInt("ilosc_dostepna");
        verify(mockResultSet, times(2)).getDate("data_dodania");
        // Upewniamy się, że nie było żadnych nieoczekiwanych interakcji
        //verifyNoMoreInteractions(mockConnectionProvider, mockConnection, mockStatement, mockResultSet);
    }

    @Test
    void shouldReturnEmptyListWhenNoData() throws SQLException {
        // Symulujemy, że ResultSet nie ma żadnych danych (next() od razu zwróci false)
        when(mockResultSet.next()).thenReturn(false);

        List<Towar> towary = shopRepository.getTowary(); // Wywołujemy metodę bez argumentów

        assertEquals(0, towary.size());
        verify(mockConnectionProvider).getConnection();
        verify(mockResultSet, times(1)).next();
        //verifyNoMoreInteractions(mockConnectionProvider, mockConnection, mockStatement, mockResultSet);
    }

    @Test
    void shouldThrowSQLExceptionWhenConnectionProviderFails() throws SQLException {
        // Symulujemy, że ConnectionProvider rzuca SQLException
        when(mockConnectionProvider.getConnection()).thenThrow(new SQLException("Błąd połączenia z bazą danych"));

        // Oczekujemy, że metoda getTowary() rzuci wyjątek SQLException
        assertThrows(SQLException.class, () -> shopRepository.getTowary()); // Wywołujemy metodę bez argumentów

        verify(mockConnectionProvider).getConnection();
       // verifyNoMoreInteractions(mockConnectionProvider, mockConnection, mockStatement, mockResultSet);
    }
}
