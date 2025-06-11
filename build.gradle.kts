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

    implementation("org.xerial:sqlite-jdbc:3.45.2.0")    // Sterownik JDBC dla SQLite
    implementation("mysql:mysql-connector-java:8.0.33") // Sterownik JDBC dla MySQL
    implementation("org.postgresql:postgresql:42.7.3") // Sterownik JDBC dla PostgreSQL (sprawdź najnowszą stabilną wersję)

    // Zależności dla MongoDB
    implementation("org.mongodb:mongodb-driver-sync:4.11.1") // Sterownik MongoDB (synchronous)
    testImplementation("org.testcontainers:mongodb:1.21.1") // Moduł Testcontainers dla MongoDB

    // Zależności dla Redis
   // implementation("redis.clients:jedis:5.1.3") // Klient Jedis dla Redis
   // testImplementation("org.testcontainers:redis:1.21.1") // Moduł Testcontainers dla Redis

    // Zależności dla Kafka
    implementation("org.apache.kafka:kafka-clients:3.7.0") // Klient Kafka
    testImplementation("org.testcontainers:kafka:1.21.1") // Moduł Testcontainers dla Kafka

    // Mockito (do mockowania w testach jednostkowych)
    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0") // Integracja Mockito z JUnit 5

    // Testcontainers (do testów integracyjnych z Dockerem)
    testImplementation("org.testcontainers:testcontainers:1.21.1")
    testImplementation("org.testcontainers:junit-jupiter:1.21.1") // Integracja Testcontainers z JUnit 5
    testImplementation("org.testcontainers:mysql:1.21.1")        // Moduł Testcontainers dla MySQL
    testImplementation("org.testcontainers:postgresql:1.21.1") // Moduł Testcontainers dla PostgreSQL
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