package com.carrental.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * WAAROM KIEZEN WE VOOR EEN OBJECT DECLARATION:
 * Een object declaration in Kotlin is een singleton - er bestaat maar één instantie.
 * Dit is perfect voor database configuratie omdat we maar één database connectie willen.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN CLASS:
 * Als we een class zouden gebruiken, zouden we meerdere instanties kunnen maken
 * wat tot problemen zou leiden (meerdere connecties, race conditions, etc.).
 */

/**
 * Database configuratie en initialisatie.
 * Dit object zorgt voor het opzetten van de database connectie en het aanmaken van tabellen.
 */
object DatabaseConfig {
    
    /**
     * Initialiseert de database connectie.
     * We gebruiken H2 als in-memory database voor eenvoud tijdens het assessment.
     * In productie zou je een echte database zoals PostgreSQL of MySQL gebruiken.
     */
    fun init() {
        // Maak verbinding met H2 in-memory database
        // H2 is perfect voor ontwikkeling en testen omdat geen installatie nodig is
        Database.connect(
            url = "jdbc:h2:mem:carrental;DB_CLOSE_DELAY=-1;",  // In-memory database
            driver = "org.h2.Driver",
            user = "sa",
            password = ""
        )
        
        // Maak de tabellen aan als ze nog niet bestaan
        transaction {
            // SchemaUtils.create maakt tabellen aan als ze niet bestaan
            // Dit is idempotent - veilig om meerdere keren aan te roepen
            SchemaUtils.create(
                AutoTabel,
                GebruikerTabel,
                ReserveringTabel,
                GebruikerAutoTabel
            )
        }
        
        println("Database geïnitialiseerd en tabellen aangemaakt!")
    }
}
