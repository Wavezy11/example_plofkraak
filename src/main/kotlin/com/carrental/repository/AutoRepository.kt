package com.carrental.repository

import com.carrental.database.AutoTabel
import com.carrental.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * WAAROM KIEZEN WE VOOR EEN REPOSITORY PATTERN:
 * Een repository is een laag tussen de business logic en de database.
 * Dit maakt de code testbaar omdat we de database kunnen mocken.
 * Ook houdt het de database-specifieke code gescheiden van de rest van de applicatie.
 * 
 * WAAROM KIEZEN WE NIET VOOR DATABASE CODE IN ROUTES:
 * Als we direct database queries in de routes zouden schrijven,
 * zou de code moeilijk te testen zijn en zou de business logic
 * vermengd worden met database details.
 */

/**
 * Repository voor Auto database operaties.
 * Deze klasse bevat alle methodes om met de Auto tabel te communiceren.
 */
class AutoRepository {
    
    /**
     * Slaat een nieuwe auto op in de database.
     * 
     * WAAROM KIEZEN WE VOOR WHEN EXPR:
     * De when expressie is de Kotlin manier om type-checking te doen.
        Het is leesbaarder dan een reeks if-else statements.
     * 
     * WAAROM KIEZEN WE NIET VOOR IF-ELSE:
     * Met if-else zou de code er zo uitzien:
     * if (auto is ICE) { ... } else if (auto is BEV) { ... } else { ... }
     * Dit is minder leesbaar en minder Kotlin-idiomatisch.
     * 
     * @param auto De auto die opgeslagen moet worden
     * @return De ID van de opgeslagen auto
     */
    fun slaAutoOp(auto: Auto): Int {
        return transaction {
            // Insert de auto in de database
            // We gebruiken when om te bepalen welk type auto het is
            // en de juiste kolommen in te vullen
            val autoId = AutoTabel.insert {
                it[merk] = auto.merk
                it[model] = auto.model
                it[bouwjaar] = auto.bouwjaar
                it[kilometerstand] = auto.kilometerstand
                it[dagprijs] = auto.dagprijs
                
                // Type-specifieke vullen op basis van het werkelijke type
                when (auto) {
                    is ICE -> {
                        it[type] = "ICE"
                        it[brandstofType] = auto.brandstofType.name
                        it[verbruikPer100km] = auto.verbruikPer100km
                        it[co2UitstootPerKm] = auto.co2UitstootPerKm
                    }
                    is BEV -> {
                        it[type] = "BEV"
                        it[accuCapaciteitKwh] = auto.accuCapaciteitKwh
                        it[actieradiusKmBev] = auto.actieradiusKm
                        it[laadvermogenKw] = auto.laadvermogenKw
                    }
                    is FCEV -> {
                        it[type] = "FCEV"
                        it[waterstofCapaciteitKg] = auto.waterstofCapaciteitKg
                        it[actieradiusKmFcev] = auto.actieradiusKm
                        it[tankdrukBar] = auto.tankdrukBar
                    }
                    else -> {
                        // Dit zou niet moeten gebeuren omdat Auto abstract is
                        throw IllegalArgumentException("Onbekend autotype")
                    }
                }
            } get AutoTabel.id
            
            // Return de gegenereerde ID
            autoId!!
        }
    }
    
    /**
     * Haalt een auto op basis van ID uit de database.
     * 
     * @param id De ID van de auto
     * @return De auto, of null als niet gevonden
     */
    fun haalAutoOp(id: Int): Auto? {
        return transaction {
            // Selecteer de auto met de gegeven ID
            AutoTabel.select { AutoTabel.id eq id }
                .map { row -> mapRowNaarAuto(row) }
                .singleOrNull()
        }
    }
    
    /**
     * Haalt alle autos uit de database.
     * 
     * @return Een lijst van alle autos
     */
    fun haalAlleAutos(): List<Auto> {
        return transaction {
            AutoTabel.selectAll()
                .map { row -> mapRowNaarAuto(row) }
        }
    }
    
    /**
     * Zoekt autos op basis van filters.
     * Dit toont Functional Programming: we gebruiken filter op een lijst.
     * 
     * WAAROM KIEZEN WE VOOR FILTER:
     * Filter is een functionele operatie die een nieuwe lijst retourneert
     * met alleen de elementen die aan de voorwaarde voldoen.
     * Het is declaratief - we beschrijven WAT we willen, niet HOE.
     * 
     * WAAROM KIEZEN WE NIET VOOR EEN FOR LOOP:
     * Met een for loop zou de code er zo uitzien:
     * val resultaat = mutableListOf<Auto>()
     * for (auto in alleAutos) {
     *     if (auto.merk == merk) resultaat.add(auto)
     * }
     * Dit is meer boilerplate en minder leesbaar.
     * 
     * @param merk Optionele filter op merk
     * @param maxDagprijs Optionele filter op maximale dagprijs
     * @return Een gefilterde lijst van autos
     */
    fun zoekAutos(merk: String? = null, maxDagprijs: Double? = null): List<Auto> {
        val alleAutos = haalAlleAutos()
        
        // Gebruik filter om de lijst te filteren
        // We kunnen meerdere filter operaties chainen
        return alleAutos.filter { auto ->
            // Filter op merk als opgegeven
            val merkMatch = merk == null || auto.merk.equals(merk, ignoreCase = true)
            // Filter op dagprijs als opgegeven
            val prijsMatch = maxDagprijs == null || auto.dagprijs <= maxDagprijs
            // Return true alleen als alle filters matchen
            merkMatch && prijsMatch
        }
    }
    
    /**
     * Verwijderd een auto uit de database.
     * 
     * @param id De ID van de auto die verwijderd moet worden
     * @return True als verwijderd, false als niet gevonden
     */
    fun verwijderAuto(id: Int): Boolean {
        return transaction {
            val verwijderd = AutoTabel.deleteWhere { AutoTabel.id eq id }
            verwijderd > 0
        }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR EEN PRIVÉ HELPER METHODE:
     * Deze methode is alleen bedoeld voor intern gebruik binnen deze repository.
     * Door hem privé te maken, kunnen we de implementatie veranderen zonder
     * dat dit invloed heeft op de rest van de applicatie (encapsulation).
     * 
     * WAAROM KIEZEN WE NIET VOOR DE LOGICA IN ELKE METHODE:
     * Als we de row-to-auto conversie in elke methode zouden herhalen,
     * zou dit tot code duplicatie leiden. Als we dan iets willen veranderen,
     * moeten we het op meerdere plekken aanpassen.
     */
    
    /**
     * Helper methode om een database row om te zetten naar een Auto object.
     * Dit vertaalt het relationele model terug naar het OO model.
     * 
     * @param row De database row
     * @return Een Auto object (ICE, BEV of FCEV)
     */
    private fun mapRowNaarAuto(row: ResultRow): Auto {
        val type = row[AutoTabel.type]
        
        return when (type) {
            "ICE" -> {
                // Maak een ICE object van de database row
                ICE(
                    merk = row[AutoTabel.merk],
                    model = row[AutoTabel.model],
                    bouwjaar = row[AutoTabel.bouwjaar],
                    kilometerstand = row[AutoTabel.kilometerstand],
                    dagprijs = row[AutoTabel.dagprijs],
                    brandstofType = BrandstofType.valueOf(row[AutoTabel.brandstofType]!!),
                    verbruikPer100km = row[AutoTabel.verbruikPer100km]!!,
                    co2UitstootPerKm = row[AutoTabel.co2UitstootPerKm]!!
                )
            }
            "BEV" -> {
                // Maak een BEV object van de database row
                BEV(
                    merk = row[AutoTabel.merk],
                    model = row[AutoTabel.model],
                    bouwjaar = row[AutoTabel.bouwjaar],
                    kilometerstand = row[AutoTabel.kilometerstand],
                    dagprijs = row[AutoTabel.dagprijs],
                    accuCapaciteitKwh = row[AutoTabel.accuCapaciteitKwh]!!,
                    actieradiusKm = row[AutoTabel.actieradiusKmBev]!!,
                    laadvermogenKw = row[AutoTabel.laadvermogenKw]!!
                )
            }
            "FCEV" -> {
                // Maak een FCEV object van de database row
                FCEV(
                    merk = row[AutoTabel.merk],
                    model = row[AutoTabel.model],
                    bouwjaar = row[AutoTabel.bouwjaar],
                    kilometerstand = row[AutoTabel.kilometerstand],
                    dagprijs = row[AutoTabel.dagprijs],
                    waterstofCapaciteitKg = row[AutoTabel.waterstofCapaciteitKg]!!,
                    actieradiusKm = row[AutoTabel.actieradiusKmFcev]!!,
                    tankdrukBar = row[AutoTabel.tankdrukBar]!!
                )
            }
            else -> {
                throw IllegalArgumentException("Onbekend autotype: $type")
            }
        }
    }
}
