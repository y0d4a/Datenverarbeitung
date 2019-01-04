package com.example.app.storage
import com.example.app.model.frontend_endpoints.{Coordinates, CoordinatesAndID, Ep1DataJsonWrapper}
import com.example.app.model.{Float, TimedFloat}
import com.mongodb.spark.MongoSpark
import org.apache.spark.SparkContext
import org.apache.spark.sql.{DataFrame, _}


class FloatProcessor {
  /**
    * The spark session with the needed configuration to write and read from the mongodb databank
    */
  val spark: SparkSession = SparkSession.builder().master("local")
    .appName("MongoSparkConnectorIntro")
    .config("spark.mongodb.input.uri", "mongodb://abteilung6.com/ECCO.ECCO_grouped")
    .config("spark.mongodb.output.uri", "mongodb://abteilung6.com/ECCO.ECCO_grouped")
    .getOrCreate()

  /**
    * Required to transform the dataset into a case class
    */
  import spark.implicits._

  /**
    * The main dataset object that contains our case class Float, which represents
    * the entries inside our database
    */
  val main_dataset: Dataset[Float] = MongoSpark.load(spark).as[Float]

  /**
    * Processes the coordinates to be contained inside an object, which then gets contained inside another object
    * that stores the id of the float as well
    * @param source the dataset to read the data from
    * @return a dataset containing a CoordinatesAndID object
    */
  def processCoordinatesAndIDs(source: Dataset[Float]): Dataset[CoordinatesAndID] = {
    source.flatMap(float => float.getContent.map(timedfloat =>
      CoordinatesAndID(float.get_Id, Coordinates(timedfloat.getLongitude, timedfloat.getLatitude))))
  }

  /**
    * Wraps up the processed dataset to an additional object
    * It also drops all duplicates and returns the first longitude-latitude pair of the float
    * @return the object containing all the desired information about the coordinates for the first endpoint
    *         of the frontend
    */
  def retrieveCoordinatesAndIDs: Ep1DataJsonWrapper = {
    val a = processCoordinatesAndIDs(main_dataset).dropDuplicates(Array("id")).collect().toList
    Ep1DataJsonWrapper(processCoordinatesAndIDs(main_dataset).collect().toList)
  }

}

object Main {
  def main(args: Array[String]): Unit = {
    val a: FloatProcessor = new FloatProcessor
    val b = a.retrieveCoordinatesAndIDs
    println(b)
  }
}
