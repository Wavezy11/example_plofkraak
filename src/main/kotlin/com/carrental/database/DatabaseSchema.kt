package com.carrental.database

import org.jetbrains.exposed.sql.Table

/**
 * WAAROM KIEZEN WE VOOR EXPOSED:
 * Exposed is een Kotlin-specifiek SQL framework dat type-safe is.
 * In plaats van SQL strings te schrijven (wat foutgevoelig is),
 * definiëren we tabellen als Kotlin objecten met type-safe kolommen.
 * De compiler vangt fouten op voordat we de code draaien.
 * 
 * WAAROM KIEZEN WE NIET VOOR RAW SQL:
 * Met raw SQL zou je strings moeten gebruiken zoals "SELECT * FROM autos WHERE id = " + id.
 * Dit is foutgevoelig voor SQL injection en typefouten.
 * Met Exposed zijn de queries type-safe en leesbaar.
 */

/**
 * WAAROM KIEZEN WE VOOR EEN ENKELE TABEL VOOR ALLE AUTOTYPES:
 * In plaats van aparte tabellen voor ICE, BEV en FCEV, gebruiken we één tabel
 * met een type-kolom. Dit is een eenvoudige strategie die goed werkt voor dit assessment.
 * 
 * WAAROM KIEZEN WE NIET VOOR TABLE PER TYPE INHERITANCE:
 * Table-per-type inheritance zou betekenen dat we aparte tabellen maken voor ICE, BEV, FCEV
 * met een join met de Auto tabel. Dit is complexer en moeilijker uit te leggen.
 * Voor dit assessment is de single table approach simpeler en begrijpelijker.
 */

/**
 * Auto tabel - Dit vertaalt onze OO Auto klasse naar een relationele tabel.
 * Alle autotypes (ICE, BEV, FCEV) worden in deze tabel opgeslagen.
 */
object AutoTabel : Table("autos") {
    // Primaire sleutel - unieke identificatie voor elke auto
    val id = integer("id").autoIncrement()
    
    // Gemeenschappelijke kolommen voor alle autotypes
    val merk = varchar("merk", 100)          // Merk van de auto
    val model = varchar("model", 100)        // Model van de auto
    val bouwjaar = integer("bouwjaar")       // Bouwjaar
    val kilometerstand = integer("kilometerstand")  // Kilometerstand
    val dagprijs = double("dagprijs")        // Dagprijs in euro's
    
    // Type kolom om onderscheid te maken tussen ICE, BEV en FCEV
    // Dit is hoe we polymorfisme in de database vertalen
    val type = varchar("type", 20)           // "ICE", "BEV" of "FCEV"
    
    // Specifieke kolommen voor ICE (null voor andere types)
    val brandstofType = varchar("brandstof_type", 20).nullable()  // "BENZINE" of "DIESEL"
    val verbruikPer100km = double("verbruik_per_100km").nullable()
    val co2UitstootPerKm = double("co2_uitstoot_per_km").nullable()
    
    // Specifieke kolommen voor BEV (null voor andere types)
    val accuCapaciteitKwh = double("accu_capaciteit_kwh").nullable()
    val actieradiusKmBev = integer("actieradius_km_bev").nullable()
    val laadvermogenKw = double("laadvermogen_kw").nullable()
    
    // Specifieke kolommen voor FCEV (null voor andere types)
    val waterstofCapaciteitKg = double("waterstof_capaciteit_kg").nullable()
    val actieradiusKmFcev = integer("actieradius_km_fcev").nullable()
    val tankdrukBar = integer("tankdruk_bar").nullable()
    
    // Definieer de primaire sleutel
    override val primaryKey = PrimaryKey(id)
}

/**
 * Gebruiker tabel - Dit vertaalt onze OO Gebruiker klasse naar een relationele tabel.
 */
object GebruikerTabel : Table("gebruikers") {
    val id = integer("id").autoIncrement()
    val naam = varchar("naam", 100)
    val email = varchar("email", 100)
    val telefoonnummer = varchar("telefoonnummer", 20)
    
    override val primaryKey = PrimaryKey(id)
}

/**
 * Reservering tabel - Dit vertaalt onze OO Reservering klasse naar een relationele tabel.
 * De foreign key naar AutoTabel toont de associatie relatie.
 */
object ReserveringTabel : Table("reserveringen") {
    val id = integer("id").autoIncrement()
    val autoId = integer("auto_id").references(AutoTabel.id)  // Foreign key - associatie met Auto
    val startDatum = varchar("start_datum", 10)  // YYYY-MM-DD formaat
    val eindDatum = varchar("eind_datum", 10)    // YYYY-MM-DD formaat
    val huurderNaam = varchar("huurder_naam", 100)
    
    override val primaryKey = PrimaryKey(id)
}

/**
 * WAAROM KIEZEN WE VOOR EEN JUNCTION TABLE VOOR MANY-TO-MANY:
 * Een Gebruiker kan meerdere Auto's hebben, en een Auto kan door meerdere Gebruikers worden gehuurd.
 * Dit is een many-to-many relatie. In relationele databases lossen we dit op met een junction table
 * (ook wel koppeltabel of join table genoemd).
 * 
 * WAAROM KIEZEN WE NIET VOOR FOREIGN KEYS IN BEIDE TABELLEN:
 * Als we foreign keys zouden toevoegen in zowel Gebruiker als Auto tabel, zouden we
 * een one-to-many relatie creëren in plaats van many-to-many.
 * Een junction table is de juiste manier om many-to-many te modelleren.
 */

/**
 * GebruikerAuto koppeltabel - Dit vertaalt de many-to-many relatie tussen Gebruiker en Auto.
 * Een Gebruiker kan meerdere Auto's hebben, en een Auto kan door meerdere Gebruikers worden gehuurd.
 */
object GebruikerAutoTabel : Table("gebruiker_autos") {
    val gebruikerId = integer("gebruiker_id").references(GebruikerTabel.id)
    val autoId = integer("auto_id").references(AutoTabel.id)
    
    // We kunnen een composite primary key gebruiken om duplicaten te voorkomen
    override val primaryKey = PrimaryKey(gebruikerId, autoId)
}
