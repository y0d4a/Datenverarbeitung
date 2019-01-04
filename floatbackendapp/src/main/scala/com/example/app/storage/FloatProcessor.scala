package com.example.app.storage
import com.example.app.model.frontend_endpoints._
import com.example.app.model.{Float, TimedFloat, frontend_endpoints}
import com.mongodb.spark.MongoSpark
import org.apache.spark.sql.{DataFrame, _}


class FloatProcessor {
  /**
    * The spark session with the needed configuration to write and read from the mongodb databank
    */
  val spark: SparkSession = SparkSession.builder().master("local[*]")
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
  private def processCoordinatesAndIDs(source: Dataset[Float]): Dataset[CoordinatesAndID] = {
    source.flatMap(float => float.getContent.map(timedfloat =>
      CoordinatesAndID(float.get_Id, Coordinates(timedfloat.getLongitude, timedfloat.getLatitude))))
  }

  private def processMeasurementsForFloat(float_id: String): Dataset[(Array[Double], Array[Double], Array[Double])] = {
    main_dataset.filter(float => float.get_Id.equals(float_id))
      .flatMap(float => float.getContent.map(timedfloat => (timedfloat.getPsal, timedfloat.getPressure, timedfloat.getTemperature)))
  }

  private def retrieveAllCoordinatesForFloat(float_id: String): Dataset[Coordinates] = {
    main_dataset.filter(float => float.get_Id.equals(float_id))
      .flatMap(float => float.getContent.map(timedfloat => Coordinates(timedfloat.getLongitude, timedfloat.getLatitude)))
  }

  /**
    * Wraps up the processed dataset to an additional object
    * It also drops all duplicates and returns the first longitude-latitude pair of the float
    * @return the object containing all the desired information about the coordinates for the first endpoint
    *         of the frontend
    */
  def retrieveCoordinatesAndIDs: Ep1DataJsonWrapper = {
    val removed_duplicates = processCoordinatesAndIDs(main_dataset).dropDuplicates(Array("id")).collect().toList
    Ep1DataJsonWrapper(removed_duplicates)
  }

  def retrieveMeasurementsForFloat(float_id: String): Ep2DataJsonWrapper = {
    val helper = processMeasurementsForFloat(float_id)
    val salt = helper.flatMap(triple => List(triple._1)).collect()
    val pressure = helper.flatMap(triple => List(triple._2)).collect()
    val temperature = helper.flatMap(triple => List(triple._3)).collect()
    val path = retrieveAllCoordinatesForFloat(float_id).collect()
    val data = MeasurementsAndPath(salt, pressure, temperature, path)
    Ep2DataJsonWrapper(data)
  }
}

