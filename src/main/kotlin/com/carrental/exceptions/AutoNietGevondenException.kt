package com.carrental.exceptions

/**
 * WAAROM KIEZEN WE VOOR EEN CUSTOM EXCEPTION:
 * Door een custom exception te maken, kunnen we specifieke fouten afhandelen
 * op een nette manier. In plaats van generieke exceptions te gooien,
 * kunnen we nu specifiek checken op "AutoNietGevondenException".
 * 
 * WAAROM KIEZEN WE NIET VOOR EEN GENERIEKE EXCEPTION:
 * Als we IllegalArgumentException of RuntimeException zouden gebruiken,
 * zou de foutafhandeling minder specifiek zijn. We zouden dan
 * moeten checken op de foutmelding string, wat foutgevoelig is.
 */

/**
 * Custom exception voor het geval een auto niet gevonden wordt.
 */
class AutoNietGevondenException(message: String) : Exception(message)
