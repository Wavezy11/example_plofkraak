package com.carrental.exceptions

/**
 * WAAROM KIEZEN WE VOOR EEN CUSTOM EXCEPTION:
 * Zelfde reden als AutoNietGevondenException - we willen specifieke
 * fouten kunnen afhandelen op een nette manier.
 */

/**
 * Custom exception voor het geval een auto al gereserveerd is.
 */
class AutoAlGereserveerdException(message: String) : Exception(message)
