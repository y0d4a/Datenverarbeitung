package com.example.app
import com.example.app.storage.{BuoyProcessor, FloatProcessor}
import org.scalatra._

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
// JSON handling support from Scalatra
import org.scalatra.json.JacksonJsonSupport

class MyScalatraServlet extends ScalatraServlet with JacksonJsonSupport {


  //val processor: FloatProcessor = new FloatProcessor
  val bprocessor: BuoyProcessor = new BuoyProcessor

  /**
    * Sets up automatic case class to JSON output serialization, required by
    * the JValueResult trait
    */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  /**
    * The content type of the HTTP response is always adjusted to JSON
    */
  before() {
    contentType = formats("json")
  }

  options("/*"){
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
  }

  get("/last_coordinates") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    //val processor: FloatProcessor = new FloatProcessor
    bprocessor.retrieveCoordinatesAndIDs
  }

  get("/measurements/:float_id") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    bprocessor.retrievePathAndLastMeasurements(params("float_id"))
  }

  get("/measurements/:float_id/:cycle_num") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    bprocessor.retrieveMeasurements(params("float_id"), params("cycle_num"))
  }
}
