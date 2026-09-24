package com.carrental.model

/**
 * WAAROM KIEZEN WE VOOR OVEREERVING (INHERITANCE):
 * ICE (Internal Combustion Engine) is een specifiek type Auto.
 * Door overerving te gebruiken, krijgt ICE automatisch alle eigenschappen van Auto
 * (merk, model, bouwjaar, kilometerstand, dagprijs) zonder dat we deze opnieuw moeten definiëren.
 * Dit is het "is-een" relatie principe: een ICE IS EEN Auto.
 * 
 * WAAROM KIEZEN WE NIET VOOR COMPOSITIE:
 * Compositie is een "heeft-een" relatie. Als we compositie zouden gebruiken,
 * zou ICE een Auto-object moeten bevatten in plaats van er zelf een te zijn.
 * Dat zou onlogisch zijn omdat een auto geen auto heeft, maar een auto IS.
 */

/**
 * ICE (Internal Combustion Engine) - Een auto die op benzine of diesel rijdt.
 * Dit is een concrete subklasse van Auto.
 */
class ICE(
    merk: String,
    model: String,
    bouwjaar: Int,
    kilometerstand: Int,
    dagprijs: Double,
    
    // Specifieke eigenschappen voor een verbrandingsmotor
    val brandstofType: BrandstofType,  // Benzine of Diesel
    val verbruikPer100km: Double,       // Verbruik in liter per 100 km
    val co2UitstootPerKm: Double        // CO2 uitstoot in gram per km
) : Auto(merk, model, bouwjaar, kilometerstand, dagprijs) {
    
    /**
     * WAAROM KIEZEN WE VOOR OVERRIDE:
     * De override annotatie maakt duidelijk dat we de abstracte methode uit de parent klasse
     * overschrijven met onze eigen implementatie. Dit is verplicht voor abstracte methodes.
     * 
     * WAAROM KIEZEN WE NIET VOOR EEN NIEUWE METHODE NAAM:
     * Als we een nieuwe methode naam zouden gebruiken (bijv. berekenTCOVoorICE),
     * zouden we polymorfisme verliezen. We zouden dan specifiek moeten weten
     * of we met een ICE, BEV of FCEV te maken hebben om de juiste methode aan te roepen.
     */
    
    /**
     * Berekent de TCO voor een ICE auto.
     * Voor een verbrandingsmotor kijken we naar:
     * - Aanschafprijs (geschat op basis van dagprijs)
     * - Brandstofkosten over de levensduur
     * - Onderhoudskosten (verbrandingsmotoren hebben meer onderhoud nodig)
     * 
     * @return De TCO in euro's
     */
    override fun berekenTCO(): Double {
        // Geschatte aanschafprijs: 365 dagen * dagprijs * 5 jaar (ruwe schatting)
        val aanschafprijs = dagprijs * 365 * 5
        
        // Brandstofkosten over geschatte levensduur van 200.000 km
        val brandstofkosten = (200000.0 / 100.0) * verbruikPer100km * brandstofType.prijsPerLiter
        
        // Onderhoudskosten: verbrandingsmotoren hebben meer onderhoud
        val onderhoudskosten = 0.05 * aanschafprijs  // 5% per jaar over 5 jaar
        
        return aanschafprijs + brandstofkosten + onderhoudskosten
    }
    
    /**
     * Berekent de verbruikskosten per kilometer voor een ICE auto.
     * Dit is simpel: verbruik per 100km * prijs per liter / 100
     * 
     * @return De kosten per kilometer in euro's
     */
    override fun berekenVerbruikskostenPerKm(): Double {
        return (verbruikPer100km * brandstofType.prijsPerLiter) / 100.0
    }
    
    /**
     * Geeft een uitgebreide beschrijving van de ICE auto.
     */
    override fun toString(): String {
        return "${super.toString()} - ${brandstofType.name} - ${verbruikPer100km}L/100km"
    }
}

/**
 * WAAROM KIEZEN WE VOOR EEN ENUM:
 * Een enum is perfect voor een vaste set waarden.
 * BrandstofType kan alleen Benzine of Diesel zijn, niets anders.
 * Dit voorkomt fouten omdat je geen ongeldige waarden kunt invoeren.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN STRING:
 * Als we een string zouden gebruiken, zou iemand "benzine", "Benzine", "BENZINE"
 * of zelfs "petrol" kunnen invoeren. Dit zou tot bugs leiden.
 * Met een enum zijn de waarden strikt gedefinieerd.
 */

/**
 * Enum voor het type brandstof van een ICE auto.
 */
enum class BrandstofType(val prijsPerLiter: Double) {
    BENZINE(1.89),   // Prijs per liter in euro's
    DIESEL(1.75)     // Prijs per liter in euro's
}
