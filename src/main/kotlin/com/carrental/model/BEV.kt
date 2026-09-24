package com.carrental.model

/**
 * WAAROM KIEZEN WE VOOR OVEREERVING:
 * BEV (Battery Electric Vehicle) is een specifiek type Auto.
 * Door overerving te gebruiken, krijgt BEV automatisch alle eigenschappen van Auto.
 * Dit volgt hetzelfde principe als ICE: een BEV IS EEN Auto.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN INTERFACE:
 * Als we een interface zouden gebruiken, zouden we de eigenschappen van Auto
 * in elke subklasse moeten herdefiniëren. Dat is dubbel werk en foutgevoelig.
 */

/**
 * BEV (Battery Electric Vehicle) - Een volledig elektrische auto.
 * Dit is een concrete subklasse van Auto.
 */
class BEV(
    merk: String,
    model: String,
    bouwjaar: Int,
    kilometerstand: Int,
    dagprijs: Double,
    
    // Specifieke eigenschappen voor een elektrische auto
    val accuCapaciteitKwh: Double,    // Capaciteit van de accu in kilowattuur
    val actieradiusKm: Int,           // Hoe ver de auto kan rijden op een volle accu
    val laadvermogenKw: Double        // Hoe snel de auto kan laden in kilowatt
) : Auto(merk, model, bouwjaar, kilometerstand, dagprijs) {
    
    /**
     * WAAROM KIEZEN WE VOOR EEN ANDERE TCO BEREKENING:
     * Elektrische auto's hebben andere kosten dan verbrandingsmotoren:
     * - Geen brandstofkosten, maar stroomkosten
     * - Minder onderhoud (geen olieverversing, minder bewegende delen)
     * - Accudegradatie over tijd
     * 
     * Door polymorfisme kan elke subklasse zijn eigen logica implementeren
     * zonder dat de aanroepende code hoeft te weten welk type het is.
     */
    
    /**
     * Berekent de TCO voor een BEV auto.
     * Voor een elektrische auto kijken we naar:
     * - Aanschafprijs (vaak hoger dan ICE)
     * - Stroomkosten over de levensduur
     * - Lager onderhoud (minder bewegende delen)
     * - Potentiële accuvervanging
     * 
     * @return De TCO in euro's
     */
    override fun berekenTCO(): Double {
        // Geschatte aanschafprijs: 365 dagen * dagprijs * 5 jaar
        // Elektrische auto's zijn vaak duurder in aanschaf
        val aanschafprijs = dagprijs * 365 * 5 * 1.3  // 30% duurder
        
        // Stroomkosten over geschatte levensduur van 200.000 km
        // Gemiddeld verbruik: accuCapaciteit / actieradius * prijs per kWh
        val verbruikPerKm = accuCapaciteitKwh / actieradiusKm
        val stroomkosten = 200000.0 * verbruikPerKm * 0.35  // €0.35 per kWh
        
        // Onderhoudskosten: veel lager dan ICE
        val onderhoudskosten = 0.02 * aanschafprijs  // 2% per jaar over 5 jaar
        
        // Potentiële accuvervanging na 10 jaar (niet meegenomen in 5 jaar TCO)
        
        return aanschafprijs + stroomkosten + onderhoudskosten
    }
    
    /**
     * Berekent de verbruikskosten per kilometer voor een BEV auto.
     * Elektrische auto's hebben veel lagere kosten per kilometer.
     * 
     * @return De kosten per kilometer in euro's
     */
    override fun berekenVerbruikskostenPerKm(): Double {
        // Verbruik per km = accuCapaciteit / actieradius
        val verbruikPerKm = accuCapaciteitKwh / actieradiusKm
        // Kosten = verbruik * prijs per kWh
        return verbruikPerKm * 0.35  // €0.35 per kWh
    }
    
    /**
     * Geeft een uitgebreide beschrijving van de BEV auto.
     */
    override fun toString(): String {
        return "${super.toString()} - Elektrisch - ${actieradiusKm}km actieradius"
    }
}
