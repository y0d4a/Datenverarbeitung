package com.example.app

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.{DataFrame, Encoders, Row}
import org.apache.spark.{SparkConf, SparkContext}
import org.scalatra._

import scala.reflect.internal.util.TableDef.Column
// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
// JSON handling support from Scalatra
import org.scalatra.json._
import org.scalatra.json.JacksonJsonSupport
import org.apache.spark.sql.SparkSession
import collection.JavaConverters._

class MyScalatraServlet extends ScalatraServlet with JacksonJsonSupport {

  /**
    * The spark session with the needed configuration to write and read from the mongodb databank
    */
  val spark: SparkSession = SparkSession.builder()
    .master("local")
    .appName("MongoSparkConnectorIntro")
    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/local.float_data")
    .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/local.float_data")
    .getOrCreate()

  val sc: SparkContext = spark.sparkContext

  /**
    * The rdd object which accumulates the data from the database
    * It returns a DataFrame object, which we then manipulate to gather Row objects that are then processed into JSON
    */
  val rdd: DataFrame = MongoSpark.load(spark)

  /**
    * Case class that represents the coordinates of the floats
    * @param longitude longitude of the float
    * @param latitude latitude of the float
    */
  case class Coordinates(longitude: Double, latitude: Double)

  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  /**
    * object mapper to transform certain objects that aren't formatted properly to JSON
    */
  implicit val objectMapper: ObjectMapper = new ObjectMapper()

  /**
    * The content type of the HTTP response is always adjusted to JSON
    */
  before() {
    contentType = formats("json")
  }

  get("/taketen") {
    //val features =(rdd.first().get("features").asInstanceOf[java.util.ArrayList[Document]].asScala)
    //features.map(doc => objectMapper.writeValueAsString(doc.get("geometry")))
    val x = rdd.take(10).map(X=>X.getStruct(1)).map(X=>X.toString)
    objectMapper.writeValueAsString(x)
  }

  /**
    * GET Request that returns all coordinates in a list of Coordinates Objects
    */
  get("/coordinates") {
    val all = rdd.take(rdd.count().asInstanceOf[Int])
    all.map(row => row.getStruct(1).getStruct(0).getList(0)).map(l => Coordinates(l.get(0), l.get(1))).toList
  }

  /**
    * GET Request that returns all the last seen date and time strings in a list
    *
    */
  // TODO: The current mapping returns the time as in the mongodb (00:00:00), which is false. We need to find a way to extract the real time values
  get("/last_seen") {
    val all = rdd.take(rdd.count().asInstanceOf[Int])

    val formatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss z", Locale.US).withZone(ZoneId.of("GMT"))
    all.map(row => row.getStruct(1).getStruct(1).getString(2))
      .map(str => LocalDateTime.parse(str, formatter))
      .map(datetime => formatter.format(datetime)).toList
  }
}
