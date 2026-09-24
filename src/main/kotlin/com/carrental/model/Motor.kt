package com.carrental.model

/**
 * WAAROM KIEZEN WE VOOR COMPOSITIE:
 * Compositie is een "heeft-een" relatie waarbij het kind niet kan bestaan zonder de parent.
 * Een Motor kan niet bestaan zonder een Auto - als de Auto wordt verwijderd,
 * wordt ook de Motor verwijderd. Dit is een sterke relatie.
 * 
 * WAAROM KIEZEN WE NIET VOOR AGGREGATIE:
 * Aggregatie is een zwakkere "heeft-een" relatie waarbij het kind wel zonder de parent kan bestaan.
 * Bijvoorbeeld: een Garage heeft Auto's, maar de Auto's kunnen ook zonder de Garage bestaan.
 * Voor Motor en Auto is compositie juist omdat een Motor altijd bij een Auto hoort.
 */

/**
 * Motor klasse - Dit toont compositie relatie met Auto.
 * Een Auto heeft een Motor, en de Motor kan niet zonder de Auto bestaan.
 */
class Motor(
    val type: MotorType,      // Type motor (benzine, diesel, elektrisch, waterstof)
    val vermogenPk: Int,      // Vermogen in pk (paardenkrachten)
    val koppelNm: Int,        // Koppel in Newtonmeter
    val cilinders: Int? = null  // Aantal cilinders (null voor elektrische motoren)
) {
    
    /**
     * Geeft een beschrijving van de motor terug.
     */
    override fun toString(): String {
        return if (cilinders != null) {
            "$type motor - $vermogenPk pk - $koppelNm Nm - $cilinders cilinders"
        } else {
            "$type motor - $vermogenPk pk - $koppelNm Nm"
        }
    }
}

/**
 * Enum voor het type motor.
 */
enum class MotorType {
    BENZINE,      // Verbrandingsmotor op benzine
    DIESEL,       // Verbrandingsmotor op diesel
    ELEKTRISCH,   // Elektrische motor
    WATERSTOF     // Brandstofcel motor
}
