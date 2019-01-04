package com.example.app
import com.example.app.storage.FloatProcessor
import org.scalatra._

// JSON-related libraries
import org.json4s.{DefaultFormats, Formats}
// JSON handling support from Scalatra
import org.scalatra.json.JacksonJsonSupport

class MyScalatraServlet extends ScalatraServlet with JacksonJsonSupport {


  val processor: FloatProcessor = new FloatProcessor

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

  get("/last_coordinates") {
    processor.retrieveCoordinatesAndIDs
  }

  get("/measurements/:float_id") {
    processor.retrieveMeasurementsForFloat(params("float_id"))
  }

}
