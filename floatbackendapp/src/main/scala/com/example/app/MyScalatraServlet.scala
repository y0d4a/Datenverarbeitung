package com.example.app

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

  val spark: SparkSession = SparkSession.builder()
    .master("local")
    .appName("MongoSparkConnectorIntro")
    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/local.float_data")
    .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/local.float_data")
    .getOrCreate()

  val sc: SparkContext = spark.sparkContext

  val rdd: DataFrame = MongoSpark.load(spark)

  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  implicit val objectMapper: ObjectMapper = new ObjectMapper()

  before() {
    contentType = formats("json")
  }


  get("/") {
    val a =rdd.take(rdd.count().asInstanceOf[Int])
    val coordinates = a.map(row => row.getStruct(1).getStruct(0).getList(0))
    objectMapper.writeValueAsString(coordinates)
  }

  get("/taketen") {
    //val features =(rdd.first().get("features").asInstanceOf[java.util.ArrayList[Document]].asScala)
    //features.map(doc => objectMapper.writeValueAsString(doc.get("geometry")))
    val x = rdd.take(10).map(X=>X.getStruct(1)).map(X=>X.toString)
    objectMapper.writeValueAsString(x)
  }

  get("/all") {
    val a =rdd.take(rdd.count().asInstanceOf[Int])
    val coordinates = a.map(row => row.getStruct(2).getStruct(0).getList(0))
    objectMapper.writeValueAsString(coordinates)
  }

}
