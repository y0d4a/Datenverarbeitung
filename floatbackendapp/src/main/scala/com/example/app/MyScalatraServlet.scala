package com.example.app

import org.json4s.{DefaultFormats, Formats}
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.scalatra._
import org.scalatra.json.JacksonJsonSupport

class MyScalatraServlet extends ScalatraServlet with JacksonJsonSupport {

  /**
    * The mongoClient connects to MongoDB's server. The constructor is empty, meaning that it connects to localhost on 27017
    */
  val mongoClient: MongoClient = MongoClient()

  /**
    * The database registered in the localhost mdb instance
    */
  val database: MongoDatabase = mongoClient.getDatabase("argo_test")

  /**
    * The collection (like a table in a relational db) that stores the fake argo data.
    */
  val argo_collection: MongoCollection[Document] = database.getCollection("float_data")

 
  get("/last_seen") {
    val observable: Observable[Document] = argo_collection.find()
    
    val l: List[Document] = Nil
    val observer: Observer[Document] = new Observer[Document] {
      override def onNext(result: Document): Unit = result::l

      override def onError(e: Throwable): Unit = println("Error occured")

      override def onComplete(): Unit = println("Query executed")
    }

    observable.subscribe(observer)
    l.foreach(docu => println(docu.toJson()))
    println("test")
  }



  /**
    * Allows the controller to automatically convert Scalatra action results to JSON.
    */
  protected implicit lazy val jsonFormats: Formats = DefaultFormats.withBigDecimal

  /**
    * Content type for all actions inside this controller will be formatted to JSON.
    */
  before() {
    contentType = formats("json")
  }

}