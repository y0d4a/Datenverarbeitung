package com.example.app

import com.fasterxml.jackson.databind.ObjectMapper
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.config._
import com.mongodb.spark.rdd.MongoRDD
import org.apache.spark.{SparkConf, SparkContext}
import org.bson.Document
import org.scalatra._
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
    .config("spark.mongodb.input.uri", "mongodb://127.0.0.1/argo_test.float_data")
    .config("spark.mongodb.output.uri", "mongodb://127.0.0.1/argo_test.float_data")
    .getOrCreate()

  val sc: SparkContext = spark.sparkContext

  val rdd: MongoRDD[Document] = MongoSpark.load(sc)

  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  implicit val objectMapper: ObjectMapper = new ObjectMapper()

  before() {
    contentType = formats("json")
  }

  get("/") {
    //val features =(rdd.first().get("features").asInstanceOf[java.util.ArrayList[Document]].asScala)
    //features.map(doc => objectMapper.writeValueAsString(doc.get("geometry")))
    val x = rdd.take(10).map(X=>X.get("features")).map(X=>X.toString)
    objectMapper.writeValueAsString(x)
  }

  
}
