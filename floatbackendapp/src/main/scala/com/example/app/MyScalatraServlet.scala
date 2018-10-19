package com.example.app

import org.scalatra._

// JSON-related libraries

import org.json4s.{DefaultFormats, Formats}

// JSON handling support from Scalatra
import org.scalatra.json._

class MyScalatraServlet extends ScalatraServlet with JacksonJsonSupport {

  // Sets up automatic case class to JSON output serialization, required by
  // the JValueResult trait.

  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal


  // Before every action runs, set the content type to be in JSON format.
  before() {
    contentType = formats("json")
  }

  get("/") {
    argoFloatData.all
  }



}



// A argoFloat object to use as a faked-out data model
case class argoFloat(floatid: String, name: String)

object argoFloatData {

  /**
    * Some fake flowers data so we can simulate retrievals.
    */
  var all = List(
    argoFloat("float 1", "Yellow Tulip"),
    argoFloat("float 2", "Red Rose"),
    argoFloat("float 3", "Black Rose"))
}