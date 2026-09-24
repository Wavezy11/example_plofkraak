package com.carrental.model

/**
 * WAAROM KIEZEN WE VOOR EEN ABSTRACT CLASS:
 * Een abstract class is perfect als je een basisimplementatie wilt geven die alle subklassen delen,
 * maar je wilt niet dat er direct instanties van de basis worden gemaakt.
 * Hier wil ik dat Auto een basis is met gemeenschappelijke eigenschappen (merk, model, jaar),
 * maar alleen concrete autotypes (ICE, BEV, FCEV) mogen worden geïnstantieerd.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN INTERFACE:
 * Een interface heeft geen implementatie, alleen contracten. Als ik alleen een interface zou gebruiken,
 * zou ik in elke subklasse alle eigenschappen opnieuw moeten definiëren.
 * Met een abstract class kan ik de gemeenschappelijke eigenschappen één keer definiëren
 * en de subklassen erven deze automatisch.
 */

/**
 * Abstracte basisklasse voor alle autotypes.
 * Dit is de basis van onze Object Oriented hiërarchie.
 * Alle autotypes (ICE, BEV, FCEV) erven van deze klasse.
 */
abstract class Auto(
    // Gemeenschappelijke eigenschappen voor alle autotypes
    val merk: String,           // Het merk van de auto (bijv. Volkswagen, Tesla)
    val model: String,          // Het model van de auto (bijv. Golf, Model 3)
    val bouwjaar: Int,          // Het jaar waarin de auto is gebouwd
    val kilometerstand: Int,    // Huidige kilometerstand in kilometers
    val dagprijs: Double        // De huurprijs per dag in euro's
) {
    
    /**
     * WAAROM KIEZEN WE VOOR EEN ABSTRACTE METHODE:
     * Een abstracte methode dwingt elke subklasse om zijn eigen implementatie te geven.
     * Dit is polymorfisme in actie: de zelfde methodeaanroep doet verschillende dingen
     * afhankelijk van het type auto.
     * 
     * WAAROM KIEZEN WE NIET VOOR EEN CONCRETE METHODE:
     * Als ik hier een concrete methode zou maken met een standaardberekening,
     * zou ik de specifieke logica voor elk autotype moeten negeren met if/else statements.
     * Dat zou de code lelijk en moeilijk uitbreidbaar maken.
     */
    
    /**
     * Berekent de Total Cost of Ownership (TCO) van de auto.
     * Dit is een abstracte methode, dus elke subklasse moet zijn eigen berekening implementeren.
     * 
     * @return De TCO in euro's
     */
    abstract fun berekenTCO(): Double
    
    /**
     * Berekent de verbruikskosten per kilometer.
     * Dit verschilt per autotype (benzine, elektrisch, waterstof).
     * 
     * @return De kosten per kilometer in euro's
     */
    abstract fun berekenVerbruikskostenPerKm(): Double
    
    /**
     * Geeft een beschrijving van de auto terug.
     * Dit is een concrete methode die door alle subklassen wordt gedeeld.
     * 
     * @return Een tekstuele beschrijving van de auto
     */
    override fun toString(): String {
        return "$merk $model ($bouwjaar) - €$dagprijs/dag"
    }
}
