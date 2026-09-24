package com.carrental.model

/**
 * WAAROM KIEZEN WE VOOR OVEREERVING:
 * FCEV (Fuel Cell Electric Vehicle) is een specifiek type Auto.
 * Door overerving te gebruiken, krijgt FCEV automatisch alle eigenschappen van Auto.
 * Dit volgt hetzelfde principe: een FCEV IS EEN Auto.
 */

/**
 * FCEV (Fuel Cell Electric Vehicle) - Een waterstofauto.
 * Dit is een concrete subklasse van Auto.
 */
class FCEV(
    merk: String,
    model: String,
    bouwjaar: Int,
    kilometerstand: Int,
    dagprijs: Double,
    
    // Specifieke eigenschappen voor een waterstofauto
    val waterstofCapaciteitKg: Double,  // Hoeveel waterstof de tank kan bevatten in kg
    val actieradiusKm: Int,             // Hoe ver de auto kan rijden op een volle tank
    val tankdrukBar: Int                // De druk van de waterstoftank in bar
) : Auto(merk, model, bouwjaar, kilometerstand, dagprijs) {
    
    /**
     * WAAROM KIEZEN WE VOOR EEN ANDERE TCO BEREKENING:
     * Waterstofauto's hebben weer andere kosten:
     * - Hoge aanschafprijs (nieuwe technologie)
     * - Waterstof is duurder dan elektriciteit
     * - Weinig onderhoud (elektrische aandrijving)
     * - Beperkte tankinfrastructuur
     */
    
    /**
     * Berekent de TCO voor een FCEV auto.
     * Voor een waterstofauto kijken we naar:
     * - Aanschafprijs (zeer hoog door nieuwe technologie)
     * - Waterstofkosten over de levensduur
     * - Laag onderhoud (elektrische aandrijving)
     * 
     * @return De TCO in euro's
     */
    override fun berekenTCO(): Double {
        // Geschatte aanschafprijs: 365 dagen * dagprijs * 5 jaar
        // Waterstofauto's zijn veel duurder in aanschaf
        val aanschafprijs = dagprijs * 365 * 5 * 1.8  // 80% duurder
        
        // Waterstofkosten over geschatte levensduur van 200.000 km
        // Verbruik per km = waterstofCapaciteit / actieradius
        val verbruikPerKm = waterstofCapaciteitKg / actieradiusKm
        val waterstofkosten = 200000.0 * verbruikPerKm * 12.0  // €12 per kg waterstof
        
        // Onderhoudskosten: laag (elektrische aandrijving)
        val onderhoudskosten = 0.02 * aanschafprijs  // 2% per jaar over 5 jaar
        
        return aanschafprijs + waterstofkosten + onderhoudskosten
    }
    
    /**
     * Berekent de verbruikskosten per kilometer voor een FCEV auto.
     * Waterstof is duurder dan elektriciteit maar goedkoper dan benzine.
     * 
     * @return De kosten per kilometer in euro's
     */
    override fun berekenVerbruikskostenPerKm(): Double {
        // Verbruik per km = waterstofCapaciteit / actieradius
        val verbruikPerKm = waterstofCapaciteitKg / actieradiusKm
        // Kosten = verbruik * prijs per kg waterstof
        return verbruikPerKm * 12.0  // €12 per kg waterstof
    }
    
    /**
     * Geeft een uitgebreide beschrijving van de FCEV auto.
     */
    override fun toString(): String {
        return "${super.toString()} - Waterstof - ${actieradiusKm}km actieradius"
    }
}
