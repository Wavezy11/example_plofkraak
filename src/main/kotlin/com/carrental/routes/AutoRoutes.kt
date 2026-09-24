package com.carrental.routes

import com.carrental.exceptions.AutoAlGereserveerdException
import com.carrental.exceptions.AutoNietGevondenException
import com.carrental.model.*
import com.carrental.repository.AutoRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * WAAROM KIEZEN WE VOOR ROUTING:
 * Routing is de manier in Ktor om HTTP endpoints te definiëren.
 * Elke route koppelt een URL en HTTP methode (GET, POST, etc.) aan code.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN CONTROLLER:
 * Ktor gebruikt geen controllers zoals Spring. In plaats daarvan
 * gebruiken we routing functions die direct de request/response afhandelen.
 * Dit is meer functioneel en minder boilerplate.
 */

/**
 * Data class voor het ontvangen van auto data via de API.
 * Dit is een DTO (Data Transfer Object) - het vertaalt JSON naar Kotlin objecten.
 */
data class AutoDTO(
    val merk: String,
    val model: String,
    val bouwjaar: Int,
    val kilometerstand: Int,
    val dagprijs: Double,
    val type: String,  // "ICE", "BEV" of "FCEV"
    
    // ICE specifieke velden
    val brandstofType: String? = null,
    val verbruikPer100km: Double? = null,
    val co2UitstootPerKm: Double? = null,
    
    // BEV specifieke velden
    val accuCapaciteitKwh: Double? = null,
    val actieradiusKm: Int? = null,
    val laadvermogenKw: Double? = null,
    
    // FCEV specifieke velden
    val waterstofCapaciteitKg: Double? = null,
    val tankdrukBar: Int? = null
)

/**
 * Data class voor het retourneren van auto data via de API.
 */
data class AutoResponseDTO(
    val id: Int,
    val merk: String,
    val model: String,
    val bouwjaar: Int,
    val kilometerstand: Int,
    val dagprijs: Double,
    val type: String,
    val tco: Double,
    val verbruikskostenPerKm: Double
)

/**
 * Configureert alle auto-gerelateerde routes.
 * 
 * @param repository De AutoRepository die gebruikt wordt voor database operaties
 */
fun Route.autoRoutes(repository: AutoRepository) {
    
    /**
     * WAAROM KIEZEN WE VOOR ROUTE:
     * route("/autos") definieert een basispad voor alle auto-gerelateerde endpoints.
     * Sub-routes zoals "/autos/{id}" worden hieronder gedefinieerd.
     */
    
    route("/autos") {
        
        /**
         * GET /autos - Haal alle autos op.
         * 
         * WAAROM KIEZEN WE VOOR GET:
         * GET wordt gebruikt voor het ophalen van data zonder de server te wijzigen.
         * Dit is de juiste HTTP methode volgens REST principes.
         * 
         * WAAROM KIEZEN WE NIET VOOR POST:
         * POST wordt gebruikt voor het aanmaken van data. Als we GET zouden gebruiken
         * voor het aanmaken van data, zou dit tegen REST principes ingaan.
         */
        get {
            // Haal alle autos uit de database
            val autos = repository.haalAlleAutos()
            
            // WAAROM KIEZEN WE VOOR MAP:
            // Map transformeert elke auto naar een AutoResponseDTO.
            // Dit is een functionele operatie die declaratief is.
            // 
            // WAAROM KIEZEN WE NIET VOOR EEN FOR LOOP:
            // Met een for loop zouden we handmatig een nieuwe lijst moeten bouwen
            // en elke auto moeten converteren. Map is korter en leesbaarder.
            val response = autos.map { auto ->
                AutoResponseDTO(
                    id = 0,  // ID wordt niet bijgehouden in het OO model
                    merk = auto.merk,
                    model = auto.model,
                    bouwjaar = auto.bouwjaar,
                    kilometerstand = auto.kilometerstand,
                    dagprijs = auto.dagprijs,
                    type = auto::class.simpleName!!,
                    tco = auto.berekenTCO(),
                    verbruikskostenPerKm = auto.berekenVerbruikskostenPerKm()
                )
            }
            
            // Stuur de response terug als JSON
            call.respond(response)
        }
        
        /**
         * GET /autos/{id} - Haal een specifieke auto op basis van ID.
         * 
         * WAAROM KIEZEN WE VOOR PATH PARAMETERS:
         * {id} is een path parameter - deel van de URL.
         * Dit is de juiste manier om een resource te identificeren.
         * 
         * WAAROM KIEZEN WE NIET VOOR QUERY PARAMETERS:
         * Query parameters (?id=1) zijn beter voor optionele filters.
         * Voor een verplichte ID zijn path parameters beter.
         */
        get("{id}") {
            // Haal de ID uit de URL
            val id = call.parameters["id"]?.toIntOrNull()
            
            // Controleer of de ID geldig is
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Ongeldige ID")
                return@get
            }
            
            try {
                // Probeer de auto op te halen
                val auto = repository.haalAutoOp(id)
                
                if (auto == null) {
                    // Auto niet gevonden - gooi een exception
                    throw AutoNietGevondenException("Auto met ID $id niet gevonden")
                }
                
                // Stuur de auto terug
                val response = AutoResponseDTO(
                    id = id,
                    merk = auto.merk,
                    model = auto.model,
                    bouwjaar = auto.bouwjaar,
                    kilometerstand = auto.kilometerstand,
                    dagprijs = auto.dagprijs,
                    type = auto::class.simpleName!!,
                    tco = auto.berekenTCO(),
                    verbruikskostenPerKm = auto.berekenVerbruikskostenPerKm()
                )
                call.respond(response)
                
            } catch (e: AutoNietGevondenException) {
                // Behandel de custom exception
                call.respond(HttpStatusCode.NotFound, e.message ?: "Auto niet gevonden")
            }
        }
        
        /**
         * GET /autos/zoeken - Zoek autos op basis van filters.
         * 
         * WAAROM KIEZEN WE VOOR QUERY PARAMETERS:
         * Query parameters zijn perfect voor optionele filters.
         * /autos/zoeken?merk=Volkswagen&maxPrijs=50
         * 
         * WAAROM KIEZEN WE NIET VOOR PATH PARAMETERS:
         * Path parameters zijn voor verplichte waarden. Filters zijn optioneel,
         * dus query parameters zijn de juiste keuze.
         */
        get("zoeken") {
            // Haal de query parameters op
            val merk = call.request.queryParameters["merk"]
            val maxDagprijs = call.request.queryParameters["maxDagprijs"]?.toDoubleOrNull()
            
            // Zoek de autos met de filters
            val autos = repository.zoekAutos(merk, maxDagprijs)
            
            // Transformeer naar response DTOs
            val response = autos.map { auto ->
                AutoResponseDTO(
                    id = 0,
                    merk = auto.merk,
                    model = auto.model,
                    bouwjaar = auto.bouwjaar,
                    kilometerstand = auto.kilometerstand,
                    dagprijs = auto.dagprijs,
                    type = auto::class.simpleName!!,
                    tco = auto.berekenTCO(),
                    verbruikskostenPerKm = auto.berekenVerbruikskostenPerKm()
                )
            }
            
            call.respond(response)
        }
        
        /**
         * POST /autos - Voeg een nieuwe auto toe.
         * 
         * WAAROM KIEZEN WE VOOR POST:
         * POST wordt gebruikt voor het aanmaken van nieuwe resources.
         * Dit is de juiste HTTP methode volgens REST principes.
         */
        post {
            try {
                // Ontvang de JSON body en parse naar AutoDTO
                val dto = call.receive<AutoDTO>()
                
                // Converteer de DTO naar een Auto object
                // WAAROM KIEZEN WE VOOR WHEN:
                // We gebruiken when om het juiste autotype te instantiëren
                // op basis van het type veld in de DTO.
                val auto = when (dto.type.uppercase()) {
                    "ICE" -> {
                        // Controleer of alle ICE-specifieke velden aanwezig zijn
                        if (dto.brandstofType == null || dto.verbruikPer100km == null || dto.co2UitstootPerKm == null) {
                            call.respond(HttpStatusCode.BadRequest, "ICE auto vereist brandstofType, verbruikPer100km en co2UitstootPerKm")
                            return@post
                        }
                        ICE(
                            merk = dto.merk,
                            model = dto.model,
                            bouwjaar = dto.bouwjaar,
                            kilometerstand = dto.kilometerstand,
                            dagprijs = dto.dagprijs,
                            brandstofType = BrandstofType.valueOf(dto.brandstofType!!.uppercase()),
                            verbruikPer100km = dto.verbruikPer100km!!,
                            co2UitstootPerKm = dto.co2UitstootPerKm!!
                        )
                    }
                    "BEV" -> {
                        // Controleer of alle BEV-specifieke velden aanwezig zijn
                        if (dto.accuCapaciteitKwh == null || dto.actieradiusKm == null || dto.laadvermogenKw == null) {
                            call.respond(HttpStatusCode.BadRequest, "BEV auto vereist accuCapaciteitKwh, actieradiusKm en laadvermogenKw")
                            return@post
                        }
                        BEV(
                            merk = dto.merk,
                            model = dto.model,
                            bouwjaar = dto.bouwjaar,
                            kilometerstand = dto.kilometerstand,
                            dagprijs = dto.dagprijs,
                            accuCapaciteitKwh = dto.accuCapaciteitKwh!!,
                            actieradiusKm = dto.actieradiusKm!!,
                            laadvermogenKw = dto.laadvermogenKw!!
                        )
                    }
                    "FCEV" -> {
                        // Controleer of alle FCEV-specifieke velden aanwezig zijn
                        if (dto.waterstofCapaciteitKg == null || dto.actieradiusKm == null || dto.tankdrukBar == null) {
                            call.respond(HttpStatusCode.BadRequest, "FCEV auto vereist waterstofCapaciteitKg, actieradiusKm en tankdrukBar")
                            return@post
                        }
                        FCEV(
                            merk = dto.merk,
                            model = dto.model,
                            bouwjaar = dto.bouwjaar,
                            kilometerstand = dto.kilometerstand,
                            dagprijs = dto.dagprijs,
                            waterstofCapaciteitKg = dto.waterstofCapaciteitKg!!,
                            actieradiusKm = dto.actieradiusKm!!,
                            tankdrukBar = dto.tankdrukBar!!
                        )
                    }
                    else -> {
                        call.respond(HttpStatusCode.BadRequest, "Ongeldig autotype: ${dto.type}. Gebruik ICE, BEV of FCEV")
                        return@post
                    }
                }
                
                // Sla de auto op in de database
                val id = repository.slaAutoOp(auto)
                
                // Stuur een succesvolle response terug
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "bericht" to "Auto succesvol toegevoegd"))
                
            } catch (e: Exception) {
                // Behandel eventuele fouten
                call.respond(HttpStatusCode.BadRequest, "Fout bij toevoegen auto: ${e.message}")
            }
        }
        
        /**
         * DELETE /autos/{id} - Verwijder een auto.
         * 
         * WAAROM KIEZEN WE VOOR DELETE:
         * DELETE wordt gebruikt voor het verwijderen van resources.
         * Dit is de juiste HTTP methode volgens REST principes.
         */
        delete("{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, "Ongeldige ID")
                return@delete
            }
            
            try {
                // Controleer of de auto bestaat
                val auto = repository.haalAutoOp(id)
                if (auto == null) {
                    throw AutoNietGevondenException("Auto met ID $id niet gevonden")
                }
                
                // Verwijder de auto
                val verwijderd = repository.verwijderAuto(id)
                
                if (verwijderd) {
                    call.respond(HttpStatusCode.OK, "Auto succesvol verwijderd")
                } else {
                    call.respond(HttpStatusCode.NotFound, "Auto niet gevonden")
                }
                
            } catch (e: AutoNietGevondenException) {
                call.respond(HttpStatusCode.NotFound, e.message ?: "Auto niet gevonden")
            }
        }
    }
}
