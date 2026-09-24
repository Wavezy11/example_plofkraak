package com.carrental.model

import java.time.LocalDate

/**
 * WAAROM KIEZEN WE VOOR ASSOCIATIE:
 * Een Reservering is gekoppeld aan een Auto, maar de Auto kan bestaan zonder Reservering.
 * Ook kan een Reservering worden verwijderd zonder dat de Auto wordt verwijderd.
 * Dit is een zwakke relatie - de objecten zijn onafhankelijk.
 * 
 * WAAROM KIEZEN WE NIET VOOR COMPOSITIE:
 * Als we compositie zouden gebruiken, zou de Auto worden verwijderd
 * wanneer de Reservering wordt verwijderd. Dat is niet logisch - een Auto
 * moet bestaan ongeacht of er reserveringen zijn.
 */

/**
 * Reservering klasse - Dit toont associatie relatie met Auto.
 * Een Reservering is gekoppeld aan een Auto voor een bepaalde periode.
 */
class Reservering(
    val id: Int,              // Unieke identificatie van de reservering
    val auto: Auto,           // De auto die gereserveerd is (associatie)
    val startDatum: LocalDate,  // Startdatum van de reservering
    val eindDatum: LocalDate,   // Einddatum van de reservering
    val huurderNaam: String     // Naam van de persoon die reserveert
) {
    
    /**
     * Berekent het aantal dagen van de reservering.
     * 
     * @return Het aantal dagen
     */
    fun berekenAantalDagen(): Long {
        return java.time.temporal.ChronoUnit.DAYS.between(startDatum, eindDatum) + 1
    }
    
    /**
     * Berekent de totale kosten van de reservering.
     * Dit is het aantal dagen maal de dagprijs van de auto.
     * 
     * @return De totale kosten in euro's
     */
    fun berekenTotaleKosten(): Double {
        return berekenAantalDagen() * auto.dagprijs
    }
    
    /**
     * Controleert of de reservering actief is op een bepaalde datum.
     * 
     * @param datum De datum om te controleren
     * @return True als de reservering actief is op deze datum, anders false
     */
    fun isActiefOp(datum: LocalDate): Boolean {
        return !datum.isBefore(startDatum) && !datum.isAfter(eindDatum)
    }
    
    /**
     * Geeft een beschrijving van de reservering terug.
     */
    override fun toString(): String {
        return "Reservering #$id: ${auto.merk} ${auto.model} van $startDatum tot $eindDatum - €${berekenTotaleKosten()}"
    }
}
