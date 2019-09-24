package com.example.app

import com.example.app.storage.BuoyProcessor
import org.apache.commons.lang.exception.ExceptionUtils.getStackTrace
import scala.io.Source
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

  options("/*") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
  }

  get("/") {
    contentType = formats("html")
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    val source = Source.fromFile("src/main/resources/html/index.html")
    val output = source.getLines().mkString;
    source.close();
    output;
  }

  get("/last_coordinates") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    try {
      Ok(bprocessor.retrieveCoordinatesAndIDs)
    }
    catch {
      case _: Throwable => InternalServerError()
    }
  }

  get("/measurements/:float_id") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    try {
      Ok(bprocessor.retrievePathAndLastMeasurements(params("float_id")))
    }
    catch {
      case _: NoSuchElementException => NotFound("This float_id does not exist in our database.")
      case _: Throwable => InternalServerError()
    }
  }

  get("/measurements/:float_id/:cycle_num") {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"))
    try {
      Ok(bprocessor.retrieveMeasurements(params("float_id"), params("cycle_num")))
    }
    catch {
      case _: NoSuchElementException => NotFound("This combination of float_id and cycle_num does not exist in our database.")
      case _: Throwable => InternalServerError()
    }
  }
}
