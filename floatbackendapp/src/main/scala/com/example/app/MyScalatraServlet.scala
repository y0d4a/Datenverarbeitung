package com.example.app

import java.time.{LocalDateTime, ZoneId}
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.example.app.storage.FloatProcessor
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
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


  val processor: FloatProcessor = new FloatProcessor

  /**
    * Sets up automatic case class to JSON output serialization, required by
    * the JValueResult trait
    */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  /**
    * Object Mapper to properly parse certain objects that aren't formatted properly to JSON
    */
  implicit val objectMapper: ObjectMapper = new ObjectMapper() with ScalaObjectMapper

  /**
    * The content type of the HTTP response is always adjusted to JSON
    */
  before() {
    contentType = formats("json")
  }

  get("/coordinates") {
    processor.extractCoordinates(processor.retrieveTimedFloats(processor.df))
  }

  get("/dates") {
    processor.parseJuldToCald(processor.retrieveTimedFloats(processor.df))
  }

  get("/temperature") {
    processor.getMeasurement(processor.retrieveTimedFloats(processor.df), "temp")
  }

  get("/salt") {
    processor.getMeasurement(processor.retrieveTimedFloats(processor.df), "salt")
  }

  get("/pressure") {
    processor.getMeasurement(processor.retrieveTimedFloats(processor.df), "pres")
  }

  get("/testcoordinates") {
    processor.retrieveCoorsAndId(processor.df)
  }

}
