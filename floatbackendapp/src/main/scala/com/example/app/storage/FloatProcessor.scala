package com.example.app.storage
import com.example.app.model.frontend_endpoints._
import com.example.app.model.{Float, TimedFloat, frontend_endpoints}
import com.mongodb.spark.MongoSpark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, _}


class FloatProcessor {

  /**
    * This object connects to the database and initializes itsself with the configurations specified
    * spark.mongodb.input.uri means that we are can write to the database
    * spark.mongodb.output.uri means that we can read from the database
    */
  val sparkSession: SparkSession = SparkSession.builder().master("local[*]")
    .appName("FloatREST_Interface").
    config("spark.mongodb.input.uri", "mongodb://abteilung6.com/ECCO.buoy")
    .config("spark.mongodb.output.uri", "mongodb://abteilung6.com/ECCO.buoy").getOrCreate()

  /**
    * This import statement is needed to convert the data coming from mongodb to our case class, which will ensure a more
    * readable and robust code structure
    */
  import sparkSession.implicits._

  /**
    * The dataset containing the floatserialnumber as a key and all floats mapped to that key as value
    */
  val floats: RDD[(String, Iterable[Float])] = MongoSpark.load(sparkSession).as[Float]
    .map(float => float.floatSerialNo -> float).rdd.groupByKey()

  /**
    * This method retrieves the id and coordinates of the last float mapped to a given floatserialnumber
    * @param source the source rdd from which the data is going to be processed
    * @return an rdd with the coordinates and the id of the float mapped to those coordinates
    */
  private def processCoordinatesAndIDsEP1(source: RDD[(String, Iterable[Float])]): RDD[CoordinatesAndID] = {
    source.map(tuple => CoordinatesAndID(tuple._1, Coordinates(tuple._2.last.longitude, tuple._2.last.latitude)))
  }

  def retrieveCoordinatesAndIDs(source: RDD[(String, Iterable[Float])]): Ep1DataJsonWrapper =
    Ep1DataJsonWrapper(processCoordinatesAndIDsEP1(source).collect())

  private def processCoordinatesAndIDsEP2(float_id: String, source: RDD[(String, Iterable[Float])]): RDD[Coordinates] = {
    source.filter(tuple => tuple._1.equals(float_id)).values.
      flatMap(floatiterable => floatiterable.
        map(float => Coordinates(float.longitude, float.latitude)))
  }

  def retrieveMeasurementsAndPath(float_id: String, source: RDD[(String, Iterable[Float])]): Ep2DataJsonWrapper = {
    val coordinates = processCoordinatesAndIDsEP2(float_id, source).collect()
    val measurements = source.filter(tuple => tuple._1.equals(float_id))
      .values.flatMap(floatiterable => floatiterable.map(float => (float.PSAL, float.PRES, float.TEMP))).collect().head
    Ep2DataJsonWrapper(MeasurementsAndPath(measurements._1, measurements._2, measurements._3, coordinates))
  }
}


