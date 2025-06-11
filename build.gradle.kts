import org.gradle.api.plugins.ApplicationExtension

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
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation ("mysql:mysql-connector-java:8.0.33")

    // Mockito (do mockowania w testach jednostkowych)
    testImplementation ("org.mockito:mockito-core:5.11.0")
    testImplementation ("org.mockito:mockito-junit-jupiter:5.11.0")

    // Testcontainers (do testów integracyjnych z Dockerem)
    testImplementation ("org.testcontainers:testcontainers:1.19.8")
    testImplementation ("org.testcontainers:junit-jupiter:1.19.8")
    testImplementation ("org.testcontainers:mysql:1.19.8")
}

// Konfiguracja do uruchamiania testów z JUnit 5
tasks.test {
    useJUnitPlatform()
}



configure<org.gradle.api.plugins.ApplicationExtension> {
    mainClass.set("gac.andrzej.sklep.ShopApplication")
    applicationName = "sklep-app"
}


tasks.jar {
    manifest {
        attributes["Main-Class"] = application.mainClass.get()
    }
}