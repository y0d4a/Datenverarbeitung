package com.example.app.storage

import java.time.{LocalDate, ZonedDateTime}
import java.time.format.DateTimeFormatter
import java.time.temporal.JulianFields

import com.example.app.model.TimedFloat
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.sql.fieldTypes.ObjectId
import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Encoders, Row, SparkSession}

import scala.collection.{immutable, mutable}

class FloatProcessor {
  /**
    * The spark session with the needed configuration to write and read from the mongodb databank
    */
  val spark: SparkSession = SparkSession.builder().master("local")
    .appName("MongoSparkConnectorIntro")
    .config("spark.mongodb.input.uri", "mongodb://abteilung6.com/ECCO.ECCO_grouped")
    .config("spark.mongodb.output.uri", "mongodb://abteilung6.com/ECCO.ECCO_grouped")
    .getOrCreate()

  val sc: SparkContext = spark.sparkContext

  /**
    * The dataframe object which accumulates the data from the database
    */
  val df: DataFrame = MongoSpark.load(spark)


  /**
    * Returns a map containing the floatSerialNumber as a key and all the TimedFloat objects mapped to it
    * @param dataframe the dataframe from which the information should be extracted
    * @return the map
    */
  def retrieveTimedFloats(dataframe: DataFrame): Map[String, List[TimedFloat]] = {
    val unprocessed_data = dataframe.take(dataframe.count().asInstanceOf[Int]).map(row => row.getAs[Seq[Row]]("content"))
    val processed_data = unprocessed_data.flatMap(seq => seq.flatMap(row => List(row)))

    processed_data.foldRight(Map.empty[String, List[TimedFloat]])((row, acc) => acc.updated(row.getAs[String]("floatSerialNo"),
      TimedFloat(
      row.getAs[Double]("longitude"), row.getAs[Double]("latitude"), row.getAs[String]("platformNumber"),
      row.getAs[String]("projectName"), row.getAs[Double]("juld"), row.getAs[String]("platformType"),
      row.getAs[Int]("configMissionNumber"), row.getAs[Int]("cycleNumber"),
      row.getAs[mutable.WrappedArray[Double]]("pres").toArray,
      row.getAs[mutable.WrappedArray[Double]]("temp").toArray,
      row.getAs[mutable.WrappedArray[Double]]("psal").toArray)
        :: acc.getOrElse(row.getAs[String]("floatSerialNo"), List())))
  }

  /**
    * Returns a map containing the floatSerialnumber as key and all coordinates extracted from the DataFrame
    * and mapped to a specific TimedFloat Object.
    * @param source the map containing our timedfloat objects
    * @return map with coordinates
    */
  def extractCoordinates(source: Map[String, List[TimedFloat]]): Map[String, List[(Double, Double)]] = {
    source.foldRight(Map.empty[String, List[(Double, Double)]])((floats, acc) =>
      acc.updated(floats._1, floats._2.flatMap(float => List((float.getLongitude, float.getLatitude)))))
  }

  /**
    * Converts the given julian Date to a gregorian date
    * READ ! =====> This method produces years like 4645, which shouldn't be the case, BUT if you use a normal internet converter, it
    * will give you the exact same value, so I assume the data inside the database is wrong
    * Internet Converter you can test with : https://www.aavso.org/jd-calculator
    * @param julianDate the julian date to be converted
    * @return a string representation of the converted date
    */
  private def utils_julianDateToZonedDateTime(julianDate: Double): String = {
    val julianDayNumber = math.floor(julianDate).toLong
    val julianDayFraction = julianDate - julianDayNumber
    val julianDayFractionToNanoSeconds = math.floor(julianDayFraction * 24 * 60 * 60 * math.pow(10, 9)).toLong

    val bcEraDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:z:G")
    // Julian Date starts on 12:00, November 24, 4714 BC of Gregorian Calendar.
    val julianDateStartDate = ZonedDateTime.parse("4714-11-24 12:00:00:GMT:BC", bcEraDateFormat)

    DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm:ss").format(julianDateStartDate.plusDays(julianDayNumber).plusNanos(julianDayFractionToNanoSeconds))
  }

  /**
    * Parses the julian date to a gregorian one with usage of our utility function
    * @param source the map which contains all the dates that need to be parsed
    * @return a map with the float serial number as key and the date mapped to it
    */
  def parseJuldToCald(source: Map[String, List[TimedFloat]]): Map[String, List[String]] = {
    source.foldRight(Map.empty[String, List[String]])((floats, acc) =>
      acc.updated(floats._1, floats._2.flatMap(float => List(utils_julianDateToZonedDateTime(float.getJuld)))))
  }

  /**
    * Returns the measurements of the floats based on the measurement specified (temp, salt or pressure)
    * @param source the timedfloats retrieved from the database
    * @param measurement the type of measurement that the user will request
    * @return a map containing a list of arrays of the measurements and the float id as key
    */
  def getMeasurement(source: Map[String, List[TimedFloat]], measurement: String): Map[String, List[Array[Double]]] = {
    measurement match {
      case "temp" => source.foldRight(Map.empty[String, List[Array[Double]]])((floats, acc) =>
        acc.updated(floats._1, acc.getOrElse(floats._1, floats._2.flatMap(float => List(float.getTemperature)))))
      case "salt" => source.foldRight(Map.empty[String, List[Array[Double]]])((floats, acc) =>
        acc.updated(floats._1, acc.getOrElse(floats._1, floats._2.flatMap(float => List(float.getPsal)))))
      case "pres" => source.foldRight(Map.empty[String, List[Array[Double]]])((floats, acc) =>
        acc.updated(floats._1, acc.getOrElse(floats._1, floats._2.flatMap(float => List(float.getPressure)))))
    }
  }

}

object Main {
  def main(args: Array[String]): Unit = {
    val a: FloatProcessor = new FloatProcessor
    val map = a.retrieveTimedFloats(a.df)
    val coordinates = a.extractCoordinates(map)
    val dates = a.parseJuldToCald(map)
    a.df.printSchema()
  }
}
