package com.example.app

import org.scalatra.test.scalatest._

class MyScalatraServletTests extends ScalatraFunSuite {

  addServlet(classOf[MyScalatraServlet], "/*")

  // run "test" in sbt shell to run these tests

  test("GET /measurements/:floatId on MyScalatraServlet should return status 200") {
    get("/measurements/7079") {
      status should equal (200)
    }
  }

  test("GET /last_coordinates on MyScalatraServlet should return status 200") {
    get("/last_coordinates") {
      status should equal (200)
    }
  }
}
