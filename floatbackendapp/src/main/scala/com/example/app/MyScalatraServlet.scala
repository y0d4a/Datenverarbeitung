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

  options("/*"){
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
  }

  get("/last_coordinates") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    processor.retrieveCoordinatesAndIDs(processor.floats)
  }

  get("/measurements/:float_id") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    processor.retrieveMeasurementsAndPath(params("float_id"), processor.floats)
  }

}
