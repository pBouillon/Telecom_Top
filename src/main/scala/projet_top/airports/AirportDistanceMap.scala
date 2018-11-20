package projet_top.airports

import scala.collection.immutable

/**
  * Classe qui représente une carte des distances entre les aéroports
  * @param airportIdToAirport map airportId <=> objet Airport, qui contient les aéroports représentés dans la carte des distances
  * @param airportIdsToDist carte des distances entre les aéroports, indentifiés par leur airportId
  */
class AirportDistanceMap(private val airportIdToAirport: immutable.Map[Int, Airport], private val airportIdsToDist: immutable.Map[(Int, Int), Double]) {
  /**
    * Retourne la distance qui sépare les deux aéroports les plus proches de la carte
    * @return la distance qui sépare les deux aéroports les plus proches de la carte
    */
  def minDistance: Double = {
    // TODO
    0.0
  }

  /**
    * Retourne la distance qui sépare les deux aéroports les plus éloignés de la carte
    * @return la distance qui sépare les deux aéroports les plus éloignés de la carte
    */
  def maxDistance: Double = {
    // TODO
    0.0
  }

  /**
    * Retourne la distance moyenne entre les aéroports de la carte
    * @return la distance moyenne entre les aéroports de la carte
    */
  def avgDistance: Double = {
    // TODO
    0.0
  }

  /**
    * Retourne la distance médiane entre les aéroports de la carte
    * @return la distance médiane entre les aéroports de la carte
    */
  def medianDistance: Double = {
    // TODO
    0.0
  }

  /**
    * Retourne l'écart-type des distances qui séparent les aéroports de la carte
    * @return l'écart-type des distances qui séparent les aéroports de la carte
    */
  def stdDev: Double = {
    // TODO
    0.0
  }

  /**
    * Retourne la distance entre les deux aéroports choisis, identifiés par leur ID.
    * @param airportIdA ID du premier aéroport
    * @param airportIdB ID du deuxième aéroport
    * @return la distance entre les deux
    */
  def apply(airportIdA: Int)(airportIdB: Int): Double = {
    // TODO
    0.0
  }

  /**
    * Retourne la distance entre les deux aéroports choisis
    * @param airportA premier aéroport
    * @param airportB deuxième aéroport
    * @return la distance entre les deux
    */
  def apply(airportA: Airport)(airportB: Airport): Double = {
    // TODO
    0.0
  }
}