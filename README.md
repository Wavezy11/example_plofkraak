# Rent My Car - Ktor Web API

Een complete Kotlin Ktor Web API voor de "Rent my car" casus, gebouwd voor een Object Oriented en Functional Programming assessment.

## Project Overzicht

Dit project demonstreert:
- **Object Oriented Programming**: Overerving, polymorfisme, associatie, aggregatie en compositie
- **Functional Programming**: map, filter, let, fold, en andere functionele operaties
- **Database**: Jetbrains Exposed als persistence layer met H2 in-memory database
- **Web API**: Ktor framework met RESTful endpoints en exception handling

## Project Structuur

```
src/main/kotlin/com/carrental/
├── Application.kt                    # Main entry point
├── model/                           # OO Domeinmodel
│   ├── Auto.kt                      # Abstracte basisklasse
│   ├── ICE.kt                       # Verbrandingsmotor (benzine/diesel)
│   ├── BEV.kt                       # Elektrische auto
│   ├── FCEV.kt                      # Waterstofauto
│   ├── Motor.kt                     # Motor klasse (compositie)
│   ├── Gebruiker.kt                 # Gebruiker klasse (associatie)
│   ├── Reservering.kt               # Reservering klasse (associatie)
│   └── Huursessie.kt                # Huursessie met ritgegevens en bonuspunten
├── database/                        # Database laag
│   ├── DatabaseSchema.kt            # Exposed tabel definities
│   └── DatabaseConfig.kt            # Database initialisatie
├── repository/                      # Repository pattern
│   └── AutoRepository.kt            # Auto database operaties
├── routes/                          # Ktor routes
│   └── AutoRoutes.kt                # Auto endpoints
├── exceptions/                      # Custom exceptions
│   ├── AutoNietGevondenException.kt
│   └── AutoAlGereserveerdException.kt
└── service/                         # Business logic
    └── AutoService.kt               # Functional Programming demonstratie
```

## Hoe te Draaien

### Vereisten
- Java 17 of hoger
- Kotlin 1.9.22 of hoger

### Stap 1: Dependencies installeren
De dependencies zijn al gedefinieerd in `build.gradle.kts`. Gradle zal deze automatisch downloaden.

### Stap 2: De applicatie starten
Gebruik Gradle om de applicatie te starten:

```bash
./gradlew run
```

Of als je Windows gebruikt zonder Gradle wrapper:

```bash
gradle run
```

De server start op `http://localhost:8080`

## API Endpoints

### GET /
Welkomstboodschap met beschikbare endpoints.

### GET /autos
Haal alle autos op.

**Response:**
```json
[
  {
    "id": 1,
    "merk": "Volkswagen",
    "model": "Golf",
    "bouwjaar": 2020,
    "kilometerstand": 45000,
    "dagprijs": 45.0,
    "type": "ICE",
    "tco": 82125.0,
    "verbruikskostenPerKm": 0.12285
  }
]
```

### GET /autos/{id}
Haal een specifieke auto op basis van ID.

### GET /autos/zoeken?merk=X&maxDagprijs=Y
Zoek autos op basis van filters.

**Voorbeeld:**
```
GET /autos/zoeken?merk=Volkswagen&maxDagprijs=50
```

### POST /autos
Voeg een nieuwe auto toe.

**Request Body (ICE):**
```json
{
  "merk": "BMW",
  "model": "320i",
  "bouwjaar": 2021,
  "kilometerstand": 35000,
  "dagprijs": 60.0,
  "type": "ICE",
  "brandstofType": "BENZINE",
  "verbruikPer100km": 7.0,
  "co2UitstootPerKm": 130.0
}
```

**Request Body (BEV):**
```json
{
  "merk": "BMW",
  "model": "i3",
  "bouwjaar": 2022,
  "kilometerstand": 15000,
  "dagprijs": 65.0,
  "type": "BEV",
  "accuCapaciteitKwh": 42.0,
  "actieradiusKm": 310,
  "laadvermogenKw": 50.0
}
```

**Request Body (FCEV):**
```json
{
  "merk": "Hyundai",
  "model": "Nexo",
  "bouwjaar": 2023,
  "kilometerstand": 5000,
  "dagprijs": 80.0,
  "type": "FCEV",
  "waterstofCapaciteitKg": 6.3,
  "actieradiusKm": 600,
  "tankdrukBar": 700
}
```

### DELETE /autos/{id}
Verwijder een auto.

## OO Concepten Gedemonstreerd

### 1. Overerving (Inheritance)
- `Auto` is een abstracte basisklasse
- `ICE`, `BEV`, `FCEV` erven van `Auto`
- "Is-een" relatie: een ICE IS EEN Auto

### 2. Polymorfisme
- `berekenTCO()` en `berekenVerbruikskostenPerKm()` zijn abstracte methodes
- Elke subklasse implementeert deze op zijn eigen manier
- Dezelfde methodeaanroep doet verschillende dingen per type

### 3. Compositie
- `Auto` heeft een `Motor`
- De Motor kan niet zonder de Auto bestaan
- Sterke "heeft-een" relatie

### 4. Associatie
- `Gebruiker` heeft `Auto`s
- `Reservering` is gekoppeld aan een `Auto`
- Zwakke "heeft-een" relatie - objecten kunnen onafhankelijk bestaan

### 5. Aggregatie
- `GebruikerAutoTabel` is een junction table voor many-to-many
- Een Gebruiker kan meerdere Auto's hebben, en vice versa

## FP Concepten Gedemonstreerd

### 1. Map
Transformeert elk element in een collectie:
```kotlin
autos.map { auto -> auto.toString() }
```

### 2. Filter
Selecteert elementen die aan een voorwaarde voldoen:
```kotlin
autos.filter { it.dagprijs <= maxPrijs }
```

### 3. Let
Null-safe scope function:
```kotlin
auto?.let { berekenDetails(it) }
```

### 4. Fold (Reduce)
Combineert alle elementen tot één waarde:
```kotlin
autos.fold(0.0) { totaal, auto -> totaal + auto.dagprijs }
```

### 5. Method Chaining
Combineert meerdere FP operaties:
```kotlin
autos.filter { it.dagprijs <= maxPrijs }
    .map { it.dagprijs }
    .fold(0.0) { totaal, prijs -> totaal + prijs }
```

## Casus Logica: Huursessie

De `Huursessie` klasse demonstreert:
- Bijhouden van ritgegevens (afstand, snelheid, remmingen)
- Bonuspunten toekennen bij rustig rijden

**Voorbeeld gebruik:**
```kotlin
val auto = BEV(...)
val sessie = Huursessie(auto)

sessie.voegRitToe(RitGegevens(
    afstandKm = 50.0,
    gemiddeldeSnelheid = 75.0,
    maxSnelheid = 95.0,
    aantalRemmingen = 20,
    aantalVersnellingen = 0
))

println(sessie.toString())
```

## Database Schema

De database gebruikt Exposed met de volgende tabellen:
- `autos` - Alle autotypes in één tabel (single table inheritance)
- `gebruikers` - Gebruikers
- `reserveringen` - Reserveringen met foreign key naar autos
- `gebruiker_autos` - Junction table voor many-to-many relatie

## Assessment Tips

### OO Vragen
- **Waarom abstract class?** Om gemeenschappelijke eigenschappen te delen en instantiatie te voorkomen
- **Waarom polymorfisme?** Om type-specifieke logica te implementeren zonder if/else
- **Verschil associatie vs compositie?** Associatie = zwak, objecten onafhankelijk. Compositie = sterk, kind kan niet zonder parent

### FP Vragen
- **Waarom map vs for loop?** Map is declaratief, minder boilerplate, meer idiomatisch
- **Waarom filter vs if?** Filter retourneert nieuwe lijst, immutable, functioneel
- **Waarom fold?** Om een collectie te aggregeren tot één waarde

### Database Vragen
- **Waarom single table inheritance?** Simpel, één query, goed voor dit assessment
- **Waarom Exposed?** Type-safe, Kotlin-idiomatisch, geen SQL strings
- **Waarom junction table?** Om many-to-many relatie te modelleren

## License

Dit project is gemaakt voor educatieve doeleinden.
