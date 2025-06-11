plugins {
    id("java")           // Włącza wsparcie dla projektów Java
   // id("application")    // Umożliwia uruchamianie aplikacji z metodą main
}

group = "gac.andrzej"    // Grupa projektu
version = "1.0-SNAPSHOT" // Wersja projektu

repositories {
    mavenCentral()        // Główne repozytorium do pobierania zależności
}

// Zależności do kompilacji i uruchomienia aplikacji
dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation ("mysql:mysql-connector-java:8.0.33")// Sterownik JDBC dla MySQL

    // Zależności testowe - scope 'test' oznacza, że są używane tylko podczas testowania
    // JUnit 5
    //testImplementation 'org.junit.jupiter:junit-jupiter-api:5.11.0-M1' // API JUnit 5
   //testRuntimeOnly 'org.junit.jupiter:junit-jupiter-engine:5.11.0-M1' // Silnik JUnit 5 do uruchamiania testów

    // Mockito (do mockowania w testach jednostkowych)
    testImplementation ("org.mockito:mockito-core:5.11.0")
    testImplementation ("org.mockito:mockito-junit-jupiter:5.11.0") // Integracja Mockito z JUnit 5

    // Testcontainers (do testów integracyjnych z Dockerem)
    testImplementation ("org.testcontainers:testcontainers:1.19.8")
    testImplementation ("org.testcontainers:junit-jupiter:1.19.8")// Integracja Testcontainers z JUnit 5
    testImplementation ("org.testcontainers:mysql:1.19.8")        // Moduł Testcontainers dla MySQL

}

// Konfiguracja do uruchamiania testów z JUnit 5
tasks.test {
    useJUnitPlatform()
}

// Konfiguracja dla wtyczki 'application' - wskazuje główną klasę z metodą main
//application {
//    mainClass = 'gac.andrzej.sklep.ShopApplication'
//}