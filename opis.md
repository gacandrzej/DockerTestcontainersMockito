## Kompleksowe rozwiązanie:

Zrefaktoryzowany kod aplikacji 
( ShopRepository, 
  ConnectionProvider, 
  ShopApplication): 

1. Jest bardziej modularny, czytelny i testowalny.

   - Plik build.gradle: Standardowe narzędzie do budowania i zarządzania zależnościami w Javie/Kotlinie.

   - Testy jednostkowe (ShopRepositoryTest): Szybkie, izolowane testy logiki biznesowej z użyciem Mockito.

   - Testy integracyjne (ShopRepositoryIntegrationTest): Wiarygodne testy z użyciem Testcontainers, uruchamiające prawdziwą bazę danych MySQL w kontenerze.

   - Workflow GitHub Actions: Automatyzuje cały proces budowania, testowania i inicjalizacji środowiska (z bazą danych) w CI, wykorzystując możliwości Gradle i Dockera.