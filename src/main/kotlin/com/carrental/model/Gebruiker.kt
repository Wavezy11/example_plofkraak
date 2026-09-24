package com.carrental.model

/**
 * WAAROM KIEZEN WE VOOR ASSOCIATIE:
 * Associatie is een "heeft-een" relatie waarbij de objecten onafhankelijk van elkaar kunnen bestaan.
 * Een Gebruiker kan bestaan zonder Auto's, en Auto's kunnen bestaan zonder een Gebruiker.
 * Dit is een zwakke relatie - als de Gebruiker wordt verwijderd, blijven de Auto's bestaan.
 * 
 * WAAROM KIEZEN WE NIET VOOR COMPOSITIE:
 * Als we compositie zouden gebruiken, zouden alle Auto's worden verwijderd
 * wanneer de Gebruiker wordt verwijderd. Dat is niet wat we willen - Auto's
 * moeten onafhankelijk van gebruikers kunnen bestaan.
 */

/**
 * Gebruiker klasse - Dit toont associatie relatie met Auto.
 * Een Gebruiker kan meerdere Auto's hebben, maar de Auto's kunnen ook zonder de Gebruiker bestaan.
 */
class Gebruiker(
    val id: Int,              // Unieke identificatie van de gebruiker
    val naam: String,         // Naam van de gebruiker
    val email: String,        // E-mailadres van de gebruiker
    val telefoonnummer: String // Telefoonnummer van de gebruiker
) {
    // Lijst van auto's die deze gebruiker bezit (associatie - 1-op-veel)
    // Dit is een mutable lijst omdat we auto's kunnen toevoegen en verwijderen
    private val autos: MutableList<Auto> = mutableListOf()
    
    /**
     * Voegt een auto toe aan de lijst van auto's van deze gebruiker.
     * Dit toont de associatie relatie: een Gebruiker heeft Auto's.
     * 
     * @param auto De auto die toegevoegd moet worden
     */
    fun voegAutoToe(auto: Auto) {
        autos.add(auto)
    }
    
    /**
     * Verwijdert een auto uit de lijst van auto's van deze gebruiker.
     * 
     * @param auto De auto die verwijderd moet worden
     */
    fun verwijderAuto(auto: Auto) {
        autos.remove(auto)
    }
    
    /**
     * Geeft alle auto's van deze gebruiker terug.
     * We geven een kopie terug om de interne lijst te beschermen.
     * 
     * @return Een lijst van alle auto's van deze gebruiker
     */
    fun getAutos(): List<Auto> {
        return autos.toList()
    }
    
    /**
     * Geeft het aantal auto's van deze gebruiker terug.
     * 
     * @return Het aantal auto's
     */
    fun aantalAutos(): Int {
        return autos.size
    }
    
    /**
     * Geeft een beschrijving van de gebruiker terug.
     */
    override fun toString(): String {
        return "$naam ($email) - ${autos.size} auto(s)"
    }
}
