package com.example.app.storage
import com.example.app.model.frontend_endpoints.{Coordinates, CoordinatesAndID}
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

  import spark.implicits._

  val sc: SparkContext = spark.sparkContext

  val main_dataset: Dataset[Float] = MongoSpark.load(spark).as[Float]

  def retrieveCoordinatesAndIDs(source: Dataset[Float]): List[CoordinatesAndID] = {
    source.flatMap(float => float.getContent.map(timedfloat =>
      CoordinatesAndID(float.get_Id, Coordinates(timedfloat.getLongitude, timedfloat.getLatitude)))).collect().toList
  }

}

object Main {
  def main(args: Array[String]): Unit = {
    val a: FloatProcessor = new FloatProcessor
    val b = a.main_dataset.collect()
    print(b.head)
  }
}
