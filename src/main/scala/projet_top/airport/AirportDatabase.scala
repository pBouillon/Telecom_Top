package projet_top.airport

import java.io._

import com.github.tototoshi.csv.CSVReader
import projet_top.airport.airport_filters.AirportFilter
import projet_top.country.Country
import projet_top.Utils.prefixLinesWith

import scala.collection.immutable


/**
  * Objet compagnon de la classe AirportDatabase. Sert à contenir les méthodes et champs statiques.
  */
object AirportDatabase {
  /**
    * Créé un objet AirportDatabase en lisant le fichier .csv fourni en paramètre.
    *
    * @param inputFile fichier CSV dont les données sont extraites pour construire l'AirportDatabase
    * @return un objet AirportDatabase
    */
  def loadFromCSV(inputFile: File): AirportDatabase = {
    var airports: List[Airport] = Nil

    val reader = CSVReader.open(inputFile)
    reader.foreach(fields => {
      //noinspection ZeroIndexToHead
      airports = Airport(
        airportId = fields(0).toInt,
        airportName = fields(1),
        cityName = fields(2),
        countryName = fields(3),
        latitude = fields(6).toDouble,
        longitude = fields(7).toDouble
      ) :: airports
    })
    reader.close()

    AirportDatabase.fromList(airports)
  }

  /**
    * Créé un objet AirportDatabase à partir de la liste d'objets Airport passée en paramètres.
    *
    * @param airports la liste d'objets Airport qui sert à la construction de la base
    * @return un objet AirportDatabase contenant les mêmes aéroports que la liste passée en paramètres
    */
  def fromList(airports: List[Airport]): AirportDatabase = {
    val airportIdToAirport: immutable.Map[Int, Airport] =
      airports.map((airport: Airport) => (airport.airportId, airport)).toMap
    new AirportDatabase(airportIdToAirport)
  }
}

/**
  * Classe principale qui va contenir nos données d'aéroport et qui va contenir les méthodes de traitement
  * sur cette base de données.
  *
  * @param airportIdToAirport Map airportID <=> objets Airport contenants les données des aéroports
  */
class AirportDatabase private (val airportIdToAirport: immutable.Map[Int, Airport]) {
  /**
    * Retourne l'objet Airport correspondant à l'ID choisi, et lève une exception si
    * cet ID n'est pas dans la base de données.
    *
    * @param airportId ID de l'aéroport à récupérer
    * @return l'objet Airport correspondant à l'aéroport demandé.
    */
  def getAirportById(airportId: Int): Airport = {
    if (this.airportIdToAirport.contains(airportId)) {
      this.airportIdToAirport(airportId)
    } else {
      //noinspection RedundantBlock
      throw new NoSuchElementException(s"The database doesn't contain an airport with ID ${airportId}")
    }
  }

  /**
    * Retourne l'objet Airport correspondant à l'ID choisi, et lève une exception si cet ID
    * n'est pas dans la base de données.
    * Identique à AirportDatabase.getAirportById
    *
    * @param airportId ID de l'aéroport à récupérer
    * @return l'objet Airport correspondant à l'aéroport demandé.
    */
  def apply(airportId: Int): Airport = this.getAirportById(airportId)

  /**
    * Indique si l'aéroport correspondant à l'airportId choisi est présent dans la base de données
    *
    * @param airportId l'ID de l'aéroport à tester
    * @return true ssi l'aéroport est présent dans la base de données
    */
  def contains(airportId: Int): Boolean = this.airportIdToAirport.contains(airportId)

  /**
    * Indique si l'aéroport spécifié est présent dans la base de données. Lève une exception si un aéroport de
    * même airportId est présent dans la base, mais avec des données différentes
    *
    * @param airport l'aéroport à tester
    * @return true ssi l'aéroport est présent dans la base de données
    */
  def contains(airport: Airport): Boolean = {
    if (this.airportIdToAirport.contains(airport.airportId)) {
      if (this.airportIdToAirport(airport.airportId) != airport) {
        throw new RuntimeException(
          s"""Internal data corrupted: airport "${airport.airportId}" """ +
          s"""is present in the database but with different data"""
        )
      }
      else {
        true
      }
    } else {
      false
    }
  }

  /**
    * Retourne les aéroports correspondants au filtre choisi. Retourne une liste vide si aucun ne correspond.
    * Si aucun filtre n'est spécifié, retourne la liste complète des aéroports.
    *
    * @param airportFilter filtre à appliquer sur la base de données de tous les aéroports
    * @return la liste des aéroports correspondants aux filtre choisi
    */
  //noinspection ScalaUnusedSymbol
  def getSubset(airportFilter: AirportFilter = airport_filters.All): AirportDatabase = {
    new AirportDatabase(
      this.airportIdToAirport filter { case (airportId, airport) => airportFilter.accepts(airport) }
    )
  }

  /**
    * Retourne un objet AirportDistanceMap qui est une carte des distances entre les aéroports
    * de l'AirportDatabase courante.
    *
    * @return un objet AirportDistanceMap qui est une carte des distances entre les aéroports
    *         de l'AirportDatabase courante
    */
  def getDistanceMap: AirportDistanceMap = {
    new AirportDistanceMap(this)
  }

  /**
    * Retourne la liste des objets Airport contenus dans l'AirportDatabase courante
    *
    * @return la liste des objets Airport contenus dans l'AirportDatabase courante
    */
  def toList: List[Airport] = {
    this.airportIdToAirport.values.toList
  }

  /**
    * Retourne la densité d'aéroports dans le pays choisi, par rapport au champ extrait par la
    * fonction againstWhat sur l'objet Country.
    * Par exemple, pour la densité d'aéroports par rapport au nombre d'habitants en France,
    *     val france = Country("France", 65018000, 672051)
    *     val density = airportDatabase.getDensityIn(france, _.inhabitants)
    *
    * @param country objet Country qui représente le pays dans lequel on veut effectuer la mesure
    * @param againstWhat fonction qui sélectionne un champ sur l'objet Country avec lequel sera calculé la densité
    * @return la densité d'aéroports en fonction du champ extrait par la fonction againstWhat
    */
  def getDensityIn(country: Country, againstWhat: Country => Double): Double = {
    // récupère le nombre d'aéroport dans le pays ciblé
    val nbAirportInCountry = this.airportIdToAirport.count(_._2.countryName == country.countryName)
    // retourne la densité par rapport à la fonction extractrice
    nbAirportInCountry / againstWhat(country)
  }

  /**
    * Aperçu de l'objet.
    * @return un aperçu de l'objet
    */
  override def toString: String = {
    "AirportDatabase [\n" +
    s"    airports        ${this.airportIdToAirport.size}\n" +
    "\n]"
  }

  /**
    * Aperçu de l'objet complet.
    * @return un aperçu de l'objet complet
    */
  def toStringFull: String = {
    "AirportDatabase [\n" +
      s"    airports        ${this.airportIdToAirport.size}\n\n" +
      prefixLinesWith(this.toList.sortBy(_.airportId).map(_.toString).mkString(",\n"), "    ") +
      "\n]"
  }
}
