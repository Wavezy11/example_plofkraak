package com.carrental.model

/**
 * WAAROM KIEZEN WE VOOR EEN DATA CLASS:
 * Een data class is perfect voor klassen die voornamelijk data bevatten.
 * Kotlin genereert automatisch equals(), hashCode(), toString(), copy(),
 * en componentN() methods. Dit bespaart veel boilerplate code.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN REGULIERE CLASS:
 * Een reguliere class zou ons dwingen om equals(), hashCode(), etc.
 * handmatig te implementeren. Dit is foutgevoelig en veel werk.
 * Voor data-holding klassen is data class de juiste keuze.
 */

/**
 * Data class voor het bijhouden van ritgegevens tijdens een huursessie.
 * Dit is onderdeel van de casus logica: bijhouden van afstand en rijgedrag,
 * en bonuspunten toekennen bij rustig rijden.
 */
data class RitGegevens(
    val afstandKm: Double,           // Afstand van de rit in kilometers
    val gemiddeldeSnelheid: Double,  // Gemiddelde snelheid in km/u
    val maxSnelheid: Double,         // Maximum snelheid in km/u
    val aantalRemmingen: Int,        // Aantal keer geremd
    val aantalVersnellingen: Int     // Aantal keer geschakeld (voor ICE)
)

/**
 * WAAROM KIEZEN WE VOOR EEN REGULIERE CLASS:
 * Huursessie heeft business logic (methodes die dingen berekenen),
 * niet alleen data. Daarom is een reguliere class geschikter dan een data class.
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN DATA CLASS:
 * Als we een data class zouden gebruiken, zouden we de logica
 * in een aparte service moeten plaatsen. Voor deze casus
 * is het logischer om de logica bij de data te houden.
 */

/**
 * Huursessie klasse - Houdt een huursessie bij met ritgegevens en bonuspunten.
 * Dit demonstreert casus logica: bijhouden van rijgedrag en bonuspunten.
 */
class Huursessie(
    val auto: Auto,                  // De auto die gehuurd wordt
    val startTijd: Long = System.currentTimeMillis()  // Starttijd in milliseconden
) {
    // Lijst van alle ritten tijdens deze huursessie
    private val ritten: MutableList<RitGegevens> = mutableListOf()
    
    // Bonuspunten die de huurder heeft verdiend
    private var bonuspunten: Int = 0
    
    /**
     * Voegt een rit toe aan de huursessie.
     * 
     * WAAROM KIEZEN WE VOOR MUTABLE LIST:
     * We willen ritten kunnen toevoegen tijdens de huursessie.
     * Een immutable list zou dit niet toestaan.
     * 
     * WAAROM KIEZEN WE NIET VOOR IMMUTABLE LIST:
     * Als we een immutable list zouden gebruiken, zouden we
     * bij elke rit een nieuwe lijst moeten maken. Dit is inefficiënt.
     * 
     * @param rit De ritgegevens die toegevoegd moeten worden
     */
    fun voegRitToe(rit: RitGegevens) {
        ritten.add(rit)
        
        // Bereken bonuspunten op basis van het rijgedrag
        val puntenVoorDezeRit = berekenBonuspuntenVoorRit(rit)
        bonuspunten += puntenVoorDezeRit
        
        println("Rit toegevoegd: ${rit.afstandKm}km, $puntenVoorDezeRit bonuspunten")
    }
    
    /**
     * Berekent bonuspunten voor een enkele rit op basis van rijgedrag.
     * Rustig rijden levert bonuspunten op.
     * 
     * WAAROM KIEZEN WE VOOR EEN PRIVÉ METHODE:
     * Deze methode is alleen bedoeld voor intern gebruik binnen Huursessie.
     * Door hem privé te maken, kunnen we de berekening veranderen zonder
     * dat dit invloed heeft op de rest van de applicatie.
     * 
     * WAAROM KIEZEN WE NIET VOOR DE LOGICA IN VOEG RIT TOE:
     * Als we de logica direct in voegRitToe zouden plaatsen,
     * zou de methode groot en onoverzichtelijk worden.
     * Door te extraheren houden we de code schoon.
     * 
     * @param rit De ritgegevens
     * @return Het aantal bonuspunten voor deze rit
     */
    private fun berekenBonuspuntenVoorRit(rit: RitGegevens): Int {
        var punten = 0
        
        // Bonus voor rustig rijden: lage gemiddelde snelheid
        if (rit.gemiddeldeSnelheid < 80) {
            punten += 5
        } else if (rit.gemiddeldeSnelheid < 100) {
            punten += 2
        }
        
        // Bonus voor niet te hard rijden: max snelheid onder de limiet
        if (rit.maxSnelheid <= 100) {
            punten += 10
        } else if (rit.maxSnelheid <= 120) {
            punten += 5
        }
        
        // Bonus voor zuinig rijden: weinig remmingen per kilometer
        val remmingenPerKm = rit.aantalRemmingen / rit.afstandKm
        if (remmingenPerKm < 0.5) {
            punten += 5
        }
        
        // Bonus voor soepel rijden: weinig versnellingen per kilometer (alleen voor ICE)
        if (auto is ICE) {
            val versnellingenPerKm = rit.aantalVersnellingen / rit.afstandKm
            if (versnellingenPerKm < 1.0) {
                punten += 3
            }
        }
        
        return punten
    }
    
    /**
     * Berekent de totale afstand van alle ritten in deze huursessie.
     * 
     * WAAROM KIEZEN WE VOOR FOLD:
     * Fold combineert alle elementen tot één waarde.
     * Hier tellen we alle afstanden op tot een totaal.
     * 
     * WAAROM KIEZEN WE NIET VOOR EEN FOR LOOP:
     * Een for loop zou er zo uitzien:
     * var totaal = 0.0
     * for (rit in ritten) {
     *     totaal += rit.afstandKm
     * }
     * return totaal
     * Fold is meer functioneel en declaratief.
     * 
     * @return De totale afstand in kilometers
     */
    fun berekenTotaleAfstand(): Double {
        return ritten.fold(0.0) { totaal, rit ->
            totaal + rit.afstandKm
        }
    }
    
    /**
     * Berekent de gemiddelde snelheid over alle ritten.
     * 
     * WAAROM KIEZEN WE VOOR MAP EN FOLD:
     * Eerst mappen we naar snelheden, dan folden we naar een totaal.
     * Dit toont method chaining in Functional Programming.
     * 
     * @return De gemiddelde snelheid in km/u
     */
    fun berekenGemiddeldeSnelheid(): Double {
        if (ritten.isEmpty()) return 0.0
        
        val totaalSnelheid = ritten.map { it.gemiddeldeSnelheid }
            .fold(0.0) { totaal, snelheid -> totaal + snelheid }
        
        return totaalSnelheid / ritten.size
    }
    
    /**
     * Geeft het aantal bonuspunten terug.
     * 
     * @return Het aantal bonuspunten
     */
    fun getBonuspunten(): Int {
        return bonuspunten
    }
    
    /**
     * Geeft het aantal ritten terug.
     * 
     * @return Het aantal ritten
     */
    fun aantalRitten(): Int {
        return ritten.size
    }
    
    /**
     * Berekent de totale kosten van de huursessie.
     * Dit is gebaseerd op de afstand en het autotype.
     * 
     * WAAROM KIEZEN WE VOOR POLYMORFISME:
     * We gebruiken auto.berekenVerbruikskostenPerKm() die anders is
     * voor elk autotype. Dit is polymorfisme in actie.
     * 
     * @return De totale kosten in euro's
     */
    fun berekenTotaleKosten(): Double {
        val totaleAfstand = berekenTotaleAfstand()
        val kostenPerKm = auto.berekenVerbruikskostenPerKm()
        return totaleAfstand * kostenPerKm
    }
    
    /**
     * Berekent de korting op basis van bonuspunten.
     * Elke 10 bonuspunten geeft 1% korting, maximaal 20%.
     * 
     * @return De korting als percentage (0-20)
     */
    fun berekenKortingPercentage(): Double {
        val korting = (bonuspunten / 10.0).coerceAtMost(20.0)
        return korting
    }
    
    /**
     * Berekent de eindprijs na korting.
     * 
     * @return De eindprijs in euro's
     */
    fun berekenEindprijs(): Double {
        val totaalKosten = berekenTotaleKosten()
        val kortingPercentage = berekenKortingPercentage()
        val kortingBedrag = totaalKosten * (kortingPercentage / 100.0)
        return totaalKosten - kortingBedrag
    }
    
    /**
     * Geeft een samenvatting van de huursessie terug.
     * 
     * @return Een tekstuele samenvatting
     */
    override fun toString(): String {
        return """
            Huursessie Samenvatting:
            - Auto: ${auto.merk} ${auto.model}
            - Aantal ritten: ${aantalRitten()}
            - Totale afstand: ${berekenTotaleAfstand()} km
            - Gemiddelde snelheid: ${berekenGemiddeldeSnelheid()} km/u
            - Bonuspunten: $bonuspunten
            - Korting: ${berekenKortingPercentage()}%
            - Totale kosten: €${berekenTotaleKosten()}
            - Eindprijs: €${berekenEindprijs()}
        """.trimIndent()
    }
}
