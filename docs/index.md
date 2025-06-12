
---
layout: default
title: Sklep Java + Docker
---

# 🛍️ Sklep Java + Docker

Ten projekt to prosta aplikacja sklepu napisana w Javie, która łączy się z bazą danych i wyświetla dostępne towary.  
Całość można uruchomić jako kontener Docker dzięki gotowemu obrazowi na **GitHub Container Registry**.

## 📦 Obraz Docker

Obraz dostępny jest pod adresem:

```bash
ghcr.io/gacandrzej/sklep-app:latest
```

### 🔧 Jak uruchomić

Pobierz i uruchom obraz z odpowiednimi zmiennymi środowiskowymi:

```bash
docker run --rm \
  -e DB_URL="jdbc:mysql://host:port/nazwa_bazy" \
  -e DB_USER="uzytkownik" \
  -e DB_PASS="haslo" \
  ghcr.io/gacandrzej/sklep-app:latest
```

## 🧠 Główna klasa aplikacji

Aplikacja startuje w klasie `ShopApplication`:

```java
public class ShopApplication {
    public static void main(String[] args) {
        final String DB_URL = System.getenv("DB_URL");
        final String USER = System.getenv("DB_USER");
        final String PASS = System.getenv("DB_PASS");

        if (DB_URL == null || USER == null || PASS == null) {
            System.err.println("Brak wymaganych zmiennych środowiskowych");
            System.exit(1);
        }

        ConnectionProvider connectionProvider = new ActualConnectionProvider(DB_URL, USER, PASS);
        ShopRepository shopRepository = new ShopRepository(connectionProvider);

        try {
            List<Towar> towary = shopRepository.getTowary();
            towary.forEach(System.out::println);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

## 🗃️ Wymagania

- Java 17+
- Baza danych MySQL lub zgodna z JDBC
- Docker (jeśli uruchamiasz przez kontener)

## 📚 Struktura projektu

- `ShopApplication` – punkt wejścia aplikacji
- `ShopRepository` – dostęp do danych z bazy
- `Towar` – model danych produktu
- `ConnectionProvider` – abstrakcja dostępu do połączenia JDBC

---

© 2025 gac.andrzej | Kod źródłowy na [GitHub](https://github.com/gacandrzej/sklep-app)

Wersja 1.1 - z dnia 12 czerwca 2025
