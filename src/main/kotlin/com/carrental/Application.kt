package com.carrental

import com.carrental.database.DatabaseConfig
import com.carrental.model.*
import com.carrental.repository.AutoRepository
import com.carrental.routes.autoRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*

/**
 * WAAROM KIEZEN WE VOOR EEN MAIN FUNCTION:
 * De main function is het entry point van de applicatie.
 * Dit is waar alles begint: database initialisatie, server opzetten, routes configureren.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN OBJECT DECLARATION:
 * Een object declaration zou ook werken, maar een main function
 * is de standaard manier in Kotlin voor applicatie entry points.
 */

/**
 * Main function - Startpunt van de Ktor applicatie.
 * Hier initialiseren we alles en starten we de server.
 */
fun main() {
    // Stap 1: Initialiseer de database
    // Dit maakt de tabellen aan in de H2 in-memory database
    println("=== Rent My Car - Ktor Web API ===")
    println("Database initialiseren...")
    DatabaseConfig.init()
    
    // Stap 2: Maak de repository aan
    // De repository is de laag tussen business logic en database
    val autoRepository = AutoRepository()
    
    // Stap 3: Voeg wat voorbeelddata toe (voor demonstratie)
    println("Voorbeelddata toevoegen...")
    voegVoorbeeldDataToe(autoRepository)
    
    // Stap 4: Start de Ktor server
    println("Server starten op poort 8080...")
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        
        // Configureer de Ktor applicatie
        configureSerialization()
        configureRouting(autoRepository)
        
    }.start(wait = true)
}

/**
 * WAAROM KIEZEN WE VOOR EEN EXTENSION FUNCTION:
 * Extension functions stellen ons in staat om functionaliteit toe te voegen
 * aan bestaande klassen zonder ze te wijzigen.
 * configureRouting is een extension function op Application.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN REGULIERE FUNCTIE:
 * Een reguliere functie zou de Application als parameter moeten nemen:
 * fun configureRouting(app: Application, repository: AutoRepository)
 * Extension functions zijn idiomatischer in Kotlin.
 */

/**
 * Configureert de routing voor de Ktor applicatie.
 * Hier definiëren we alle HTTP endpoints.
 * 
 * @param repository De AutoRepository die gebruikt wordt
 */
fun Application.configureRouting(repository: AutoRepository) {
    
    // Installeer routing
    routing {
        
        // Voeg de auto routes toe
        // Dit voegt alle /autos endpoints toe
        autoRoutes(repository)
        
        // Root endpoint - simpele welkomstboodschap
        get("/") {
            call.respond(mapOf(
                "bericht" to "Welkom bij Rent My Car API!",
                "endpoints" to listOf(
                    "GET /autos - Haal alle autos op",
                    "GET /autos/{id} - Haal een specifieke auto op",
                    "GET /autos/zoeken?merk=X&maxDagprijs=Y - Zoek autos",
                    "POST /autos - Voeg een nieuwe auto toe",
                    "DELETE /autos/{id} - Verwijder een auto"
                )
            ))
        }
    }
}

/**
 * Configureert JSON serialization voor de Ktor applicatie.
 * Dit zorgt ervoor dat we JSON kunnen versturen en ontvangen.
 */
fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(contentType = io.ktor.http.ContentType.Application.Json)
    }
}

/**
 * Voegt voorbeelddata toe aan de database.
 * Dit is handig voor demonstratie en testen.
 * 
 * @param repository De AutoRepository
 */
fun voegVoorbeeldDataToe(repository: AutoRepository) {
    
    // Voorbeeld ICE auto (benzine)
    val golf = ICE(
        merk = "Volkswagen",
        model = "Golf",
        bouwjaar = 2020,
        kilometerstand = 45000,
        dagprijs = 45.0,
        brandstofType = BrandstofType.BENZINE,
        verbruikPer100km = 6.5,
        co2UitstootPerKm = 120.0
    )
    repository.slaAutoOp(golf)
    println("ICE auto toegevoegd: $golf")
    
    // Voorbeeld ICE auto (diesel)
    val passat = ICE(
        merk = "Volkswagen",
        model = "Passat",
        bouwjaar = 2019,
        kilometerstand = 62000,
        dagprijs = 55.0,
        brandstofType = BrandstofType.DIESEL,
        verbruikPer100km = 5.2,
        co2UitstootPerKm = 110.0
    )
    repository.slaAutoOp(passat)
    println("ICE auto toegevoegd: $passat")
    
    // Voorbeeld BEV auto (elektrisch)
    val model3 = BEV(
        merk = "Tesla",
        model = "Model 3",
        bouwjaar = 2022,
        kilometerstand = 25000,
        dagprijs = 75.0,
        accuCapaciteitKwh = 75.0,
        actieradiusKm = 450,
        laadvermogenKw = 250.0
    )
    repository.slaAutoOp(model3)
    println("BEV auto toegevoegd: $model3")
    
    // Voorbeeld BEV auto (elektrisch)
    val leaf = BEV(
        merk = "Nissan",
        model = "Leaf",
        bouwjaar = 2021,
        kilometerstand = 30000,
        dagprijs = 50.0,
        accuCapaciteitKwh = 40.0,
        actieradiusKm = 270,
        laadvermogenKw = 50.0
    )
    repository.slaAutoOp(leaf)
    println("BEV auto toegevoegd: $leaf")
    
    // Voorbeeld FCEV auto (waterstof)
    val mirai = FCEV(
        merk = "Toyota",
        model = "Mirai",
        bouwjaar = 2023,
        kilometerstand = 8000,
        dagprijs = 85.0,
        waterstofCapaciteitKg = 5.6,
        actieradiusKm = 650,
        tankdrukBar = 700
    )
    repository.slaAutoOp(mirai)
    println("FCEV auto toegevoegd: $mirai")
    
    println("Totaal aantal autos in database: ${repository.haalAlleAutos().size}")
    println()
}
