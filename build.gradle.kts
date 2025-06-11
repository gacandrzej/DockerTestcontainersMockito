//import org.gradle.api.plugins.ApplicationExtension

plugins {
    java          // Włącza wsparcie dla projektów Java
    application   // Umożliwia uruchamianie aplikacji z metodą main
}

group = "gac.andrzej"    // Grupa projektu
version = "1.0-SNAPSHOT" // Wersja projektu

repositories {
    mavenCentral()        // Główne repozytorium do pobierania zależności
}

// Zależności do kompilacji i uruchomienia aplikacji
dependencies {
    // JUnit Platform BOM dla zarządzania wersjami JUnit (zalecane)
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    // JUnit Jupiter - zawiera API i Engine
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("mysql:mysql-connector-java:8.0.33") // Sterownik JDBC dla MySQL
    implementation("org.postgresql:postgresql:42.7.3") // Sterownik JDBC dla PostgreSQL (sprawdź najnowszą stabilną wersję)

    // Mockito (do mockowania w testach jednostkowych)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0") // Integracja Mockito z JUnit 5

    // Testcontainers (do testów integracyjnych z Dockerem)
    testImplementation("org.testcontainers:testcontainers:1.19.8")
    testImplementation("org.testcontainers:junit-jupiter:1.19.8") // Integracja Testcontainers z JUnit 5
    testImplementation("org.testcontainers:mysql:1.19.8")        // Moduł Testcontainers dla MySQL
    testImplementation("org.testcontainers:postgresql:1.19.8") // Moduł Testcontainers dla PostgreSQL
}

// Konfiguracja do uruchamiania testów z JUnit 5
tasks.test {
    useJUnitPlatform()
}



// --- STANDARDOWA KONFIGURACJA PLUGINU 'application' W KOTLIN DSL ---
// Gradle powinien rozpoznać 'application' jako rozszerzenie pluginu
application {
    mainClass.set("gac.andrzej.sklep.ShopApplication") // Ustawia główną klasę aplikacji
    applicationName = "sklep-app" // Ustawia nazwę katalogu instalacji dla installDist
}

// --- STANDARDOWA KONFIGURACJA ZADANIA 'jar' W KOTLIN DSL ---
tasks.jar {
    manifest {
        // 'application' jest dostępne w tym kontekście jako odwołanie do rozszerzenia pluginu
        attributes["Main-Class"] = application.mainClass.get()
    }
}