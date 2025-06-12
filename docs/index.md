
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




## W projekcie zastosowano:

- **Testcontainers** do uruchamiania kontenera MySQL podczas testów integracyjnych.
- **JUnit 5** do testów jednostkowych i integracyjnych.
- **Gradle** jako narzędzie do budowy projektu i uruchamiania testów.
- **GitHub Actions** do ciągłej integracji (CI) z automatycznym uruchamianiem testów i budową obrazu Docker.

## Testy integracyjne

Testy integracyjne korzystają z kontenera MySQL, który jest tworzony i uruchamiany za pomocą biblioteki Testcontainers.

### Kluczowe elementy testów integracyjnych:

- Klasa `ShopRepositoryIntegrationTest` posiada adnotację `@Testcontainers`, która integruje Testcontainers z JUnit 5.
- Kontener MySQL uruchamiany jest na podstawie oficjalnego obrazu `mysql:8.0`.
- W metodzie `setUp()` inicjalizowana jest baza danych, tworzone są tabele i wstawiane przykładowe dane.
- Testy sprawdzają pobieranie towarów z bazy (`shouldReturnTowaryFromRealDatabase`) oraz obsługę sytuacji, gdy tabela jest pusta (`shouldReturnEmptyListWhenNoTowary`).

## CI z GitHub Actions

Konfiguracja pliku `.github/workflows/java-mysql-ci.yml` umożliwia:

- Uruchamianie workflow przy pushu do gałęzi `master` lub ręcznie z poziomu UI GitHub.
- Użycie usługi MySQL w kontenerze Docker dostępnej podczas działania joba.
- Konfigurację JDK 17 oraz cache Gradle dla szybszych buildów.
- Czekanie na gotowość serwera MySQL przed wykonaniem dalszych kroków.
- Inicjalizację bazy danych przy pomocy pliku `sklep.sql`.
- Kompilację projektu i uruchomienie testów (w tym testów integracyjnych).
- Publikację raportów testów w formacie JUnit XML oraz przesłanie ich do GitHub Checks.
- Budowę i publikację obrazu Docker do GitHub Container Registry (GHCR).

## Wymagania i uwagi

- W pliku workflow wykorzystywane są sekrety GitHub (`secrets.DB_USER`, `secrets.DB_PASS`, `secrets.MYSQL_ROOT_PASSWORD`), które trzeba skonfigurować w repozytorium.
- Plik `sklep.sql` powinien zawierać odpowiednie polecenia do stworzenia tabel i wstawienia danych potrzebnych do testów i uruchomienia aplikacji.
- Projekt zakłada dostęp do sieci Docker oraz poprawną konfigurację Gradle i JDK.
- Testy integracyjne uruchamiane lokalnie i w CI używają kontenera MySQL, co gwarantuje spójność środowiska testowego.

---




© 2025 gac.andrzej | Kod źródłowy na [GitHub](https://github.com/gacandrzej/sklep-app)

Wersja 1.2 - z dnia 12 czerwca 2025
