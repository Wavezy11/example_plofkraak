package com.carrental.service

import com.carrental.model.Auto
import com.carrental.model.BEV
import com.carrental.model.ICE

/**
 * WAAROM KIEZEN WE VOOR EEN SERVICE LAAG:
 * Een service laag bevat business logic die complexer is dan simpele CRUD operaties.
 * Dit houdt de routes schoon en maakt de business logic herbruikbaar.
 * 
 * WAAROM KIEZEN WE NIET VOOR LOGICA IN ROUTES:
 * Als we complexe logica in de routes zouden plaatsen,
 * zouden de routes groot en onoverzichtelijk worden.
 * Door een service laag te gebruiken, blijven de routes simpel.
 */

/**
 * Service voor auto-gerelateerde business logic.
 * Deze klasse demonstreert Functional Programming concepten.
 */
class AutoService {
    
    /**
     * WAAROM KIEZEN WE VOOR MAP:
     * Map transformeert elke element in een collectie naar een nieuwe waarde.
     * Het is een pure function - geen side effects, alleen transformatie.
     * 
     * WAAROM KIEZEN WE NIET VOOR EEN FOR LOOP:
     * Een for loop zou er zo uitzien:
     * val resultaat = mutableListOf<String>()
     * for (auto in autos) {
     *     resultaat.add(auto.toString())
     * }
     * Dit is meer boilerplate en minder declaratief.
     * Met map beschrijven we WAT we willen, niet HOE.
     * 
     * @param autos Lijst van autos
     * @return Lijst van string representaties van de autos
     */
    fun converteerAutosNaarStrings(autos: List<Auto>): List<String> {
        // Map transformeert elke Auto naar een String
        return autos.map { auto -> auto.toString() }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR FILTER:
     * Filter selecteert alleen de elementen die aan een voorwaarde voldoen.
     * Het retourneert een nieuwe lijst zonder de originele lijst te wijzigen (immutable).
     * 
     * WAAROM KIEZEN WE NIET VOOR EEN FOR LOOP:
     * Een for loop zou er zo uitzien:
     * val resultaat = mutableListOf<Auto>()
     * for (auto in autos) {
     *     if (auto.dagprijs <= maxPrijs) {
     *         resultaat.add(auto)
     *     }
     * }
     * Dit is meer boilerplate en minder leesbaar.
     * 
     * @param autos Lijst van autos
     * @param maxPrijs Maximale dagprijs
     * @return Gefilterde lijst van autos
     */
    fun filterAutosOpPrijs(autos: List<Auto>, maxPrijs: Double): List<Auto> {
        // Filter selecteert alleen autos met dagprijs <= maxPrijs
        return autos.filter { auto -> auto.dagprijs <= maxPrijs }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR FILTER MET TYPE CHECK:
     * We kunnen filter combineren met type checks (is).
     * Dit is een krachtige combinatie voor OO + FP.
     * 
     * @param autos Lijst van autos
     * @return Lijst van alleen elektrische autos (BEV)
     */
    fun filterAlleenElektrisch(autos: List<Auto>): List<BEV> {
        // Filter selecteert alleen BEV instances
        // We casten naar BEV omdat we weten dat het BEV is
        return autos.filter { it is BEV }.map { it as BEV }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR LET:
     * Let is een scope function die een object als 'it' beschikbaar maakt
     * binnen het blok. Het wordt vaak gebruikt voor null-safe operaties.
     * 
     * WAAROM KIEZEN WE NIET VOOR IF-NOT-NULL:
     * Zonder let zou de code er zo uitzien:
     * if (auto != null) {
     *     val tco = auto.berekenTCO()
     *     val kosten = auto.berekenVerbruikskostenPerKm()
     *     return "TCO: $tco, Kosten: $kosten"
     * }
     * return "Geen auto"
     * Let is korter en leesbaarder.
     * 
     * @param auto Een auto (kan null zijn)
     * @return String met TCO en verbruikskosten, of null als auto null is
     */
    fun berekenAutoDetails(auto: Auto?): String? {
        // Let voert het blok alleen uit als auto niet null is
        // Binnen het blok is auto beschikbaar als 'it'
        return auto?.let { it ->
            val tco = it.berekenTCO()
            val kosten = it.berekenVerbruikskostenPerKm()
            "TCO: €$tco, Kosten per km: €$kosten"
        }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR FOLD (REDUCE):
     * Fold (ook wel reduce genoemd) combineert alle elementen in een collectie
     * tot één waarde. Het begint met een initiële waarde en past de functie
     * iteratief toe op elk element.
     * 
     * WAAROM KIEZEN WE NIET VOOR EEN FOR LOOP:
     * Een for loop zou er zo uitzien:
     * var totaal = 0.0
     * for (auto in autos) {
     *     totaal += auto.dagprijs
     * }
     * return totaal
     * Fold is meer functioneel en declaratief.
     * 
     * @param autos Lijst van autos
     * @return De som van alle dagprijzen
     */
    fun berekenTotaalDagprijs(autos: List<Auto>): Double {
        // Fold begint met 0.0 en telt elke dagprijs op bij het totaal
        return autos.fold(0.0) { totaal, auto ->
            totaal + auto.dagprijs
        }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR FOLD MET COMPLEXE LOGICA:
     * Fold kan ook gebruikt worden voor complexere aggregaties.
     * Hier berekenen we zowel de totaalprijs als het aantal autos.
     * 
     * @param autos Lijst van autos
     * @return Een Pair met (totaalprijs, aantal)
     */
    fun berekenStatistieken(autos: List<Auto>): Pair<Double, Int> {
        // Fold begint met een Pair (0.0, 0) en bouwt dit op
        return autos.fold(Pair(0.0, 0)) { (totaal, aantal), auto ->
            Pair(totaal + auto.dagprijs, aantal + 1)
        }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR METHOD CHAINING:
     * We kunnen FP operaties chainen: filter -> map -> fold.
    * Dit is zeer krachtig en leesbaar.
     * 
     * WAAROM KIEZEN WE NIET VOOR MEERDERE LOOPS:
     * Zonder chaining zouden we meerdere loops nodig hebben:
     * 1. Loop om te filteren
     * 2. Loop om te transformeren
     * 3. Loop om te aggregeren
     * Met chaining doen we alles in één expressie.
     * 
     * @param autos Lijst van autos
     * @param maxPrijs Maximale dagprijs
     * @return De gemiddelde dagprijs van gefilterde autos
     */
    fun berekenGemiddeldePrijsVanGoedkopeAutos(autos: List<Auto>, maxPrijs: Double): Double {
        // Chain: filter -> map -> fold
        return autos
            .filter { it.dagprijs <= maxPrijs }  // Filter goedkope autos
            .map { it.dagprijs }                  // Map naar alleen prijzen
            .fold(0.0) { totaal, prijs ->         // Fold totaal
                totaal + prijs
            } / autos.filter { it.dagprijs <= maxPrijs }.size  // Deel door aantal
    }
    
    /**
     * WAAROM KIEZEN WE VOOR SORTED WITH:
     * Sorted sorteert een lijst op basis van een comparator.
     * We kunnen dit combineren met andere FP operaties.
     * 
     * @param autos Lijst van autos
     * @return Gesorteerde lijst op dagprijs (laag naar hoog)
     */
    fun sorteerAutosOpPrijs(autos: List<Auto>): List<Auto> {
        // Sorted sorteert op dagprijs
        return autos.sortedBy { it.dagprijs }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR GROUPBY:
     * GroupBy groepeert elementen op basis van een key.
     * Dit is handig voor categorisatie.
     * 
     * @param autos Lijst van autos
     * @return Een Map met type als key en lijst van autos als value
     */
    fun groepeerAutosOpType(autos: List<Auto>): Map<String, List<Auto>> {
        // GroupBy groepeert op basis van de class naam
        return autos.groupBy { it::class.simpleName!! }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR ANY:
     * Any retourneert true als minstens één element aan de voorwaarde voldoet.
     * Dit is een kort-circuit operatie - stopt zodra er een match is.
     * 
     * @param autos Lijst van autos
     * @param maxPrijs Maximale dagprijs
     * @return True als er een auto is onder de maxPrijs
     */
    fun heeftGoedkopeAuto(autos: List<Auto>, maxPrijs: Double): Boolean {
        // Any retourneert true als er een auto is met dagprijs <= maxPrijs
        return autos.any { it.dagprijs <= maxPrijs }
    }
    
    /**
     * WAAROM KIEZEN WE VOOR ALL:
     * All retourneert true als alle elementen aan de voorwaarde voldoen.
     * Dit is ook een kort-circuit operatie - stopt zodra er geen match is.
     * 
     * @param autos Lijst van autos
     * @param minPrijs Minimale dagprijs
     * @return True als alle autos boven de minPrijs liggen
     */
    fun zijnAlleAutosDuur(autos: List<Auto>, minPrijs: Double): Boolean {
        // All retourneert true als alle autos dagprijs >= minPrijs hebben
        return autos.all { it.dagprijs >= minPrijs }
    }
}
