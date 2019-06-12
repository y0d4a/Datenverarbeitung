package com.example.app.storage
import com.example.app.model.frontend_endpoints._
import com.example.app.model.{Buoy, frontend_endpoints}
import com.mongodb.spark.MongoSpark
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, _}


class FloatProcessor {

  //System.setProperty("hadoop.home.dir", "C:\\\\hadoop")

  /**
    * This object connects to the database and initializes itsself with the configurations specified
    * spark.mongodb.input.uri means that we are can write to the database
    * spark.mongodb.output.uri means that we can read from the database
    */
  val sparkSession: SparkSession = SparkSession.builder().master("local[*]")
    .appName("FloatREST_Interface")
    //.config("spark.mongodb.input.uri", "mongodb://ecco:kd23.S.W@hadoop05.f4.htw-berlin.de:27020/ecco.buoy")
    //.config("spark.mongodb.output.uri", "mongodb://ecco:kd23.S.W@hadoop05.f4.htw-berlin.de:27020/ecco.buoy")
    .config("spark.mongodb.input.uri", "mongodb://ecco:kd23.S.W@localhost:27020/ecco.buoy")
    .config("spark.mongodb.output.uri", "mongodb://ecco:kd23.S.W@localhost:27020/ecco.buoy")
    .config("spark.ui.port", "4444")
    .getOrCreate()

  /**
    * This import statement is needed to convert the data coming from mongodb to our case class, which will ensure a more
    * readable and robust code structure
    */
  import sparkSession.implicits._

  /**
    * The dataset containing the floatserialnumber as a key and all floats mapped to that key as value
    */
  val floats: RDD[(String, Iterable[Buoy])] = MongoSpark.load(sparkSession).as[Buoy]
    .map(float => float.floatSerialNo -> float).rdd.groupByKey()

  /**
    * This method retrieves the id and coordinates of the last float mapped to a given floatserialnumber
    * @param source the source rdd from which the data is going to be processed
    * @return an rdd with the coordinates and the id of the float mapped to those coordinates
    */
  private def processCoordinatesAndIDsEP1(source: RDD[(String, Iterable[Buoy])]): RDD[CoordinatesAndID] = {
    source.map(tuple => CoordinatesAndID(tuple._1, Coordinates(tuple._2.last.longitude, tuple._2.last.latitude)))
  }

  /**
    * This method wraps the coordinates and ids for endpoint 1 inside an object, which contains the array "data".
    * So processCoordinatesAndIDsEP1 returns the data array, and this method returns an object containing the data array
    * @param source the source RDD
    * @return the object containing the data array
    */
  def retrieveCoordinatesAndIDs(source: RDD[(String, Iterable[Buoy])]): Ep1DataJsonWrapper =
    Ep1DataJsonWrapper(processCoordinatesAndIDsEP1(source).collect())

  /**
    * Returns all  the coordinates that have ever been transmitted by the float with the specified float_id
    * @param float_id the float id
    * @param source the source RDD
    * @return returns
    */
  private def processCoordinatesAndIDsEP2(float_id: String, source: RDD[(String, Iterable[Buoy])]): RDD[Coordinates] = {
    source.filter(tuple => tuple._1.equals(float_id)).values.
      flatMap(floatiterable => floatiterable.
        map(float => Coordinates(float.longitude, float.latitude)))
  }

  /**
    * Here we save the coordinates for the specified float id AND we store the measurements of the float with the specified
    * float id, by filtering the floats in the databank and finding the ones that match the given id. Then we take all the
    * measurement arrays mapped to that float and we save them together with the coordinates inside the object.
    * Then we wrap the object inside the Ep2DataJsonWrapper, which is another object, because thats how the frontend
    * wanted to receive the data
    * @param float_id the float_id
    * @param source the source RDD
    * @return all coordinates mapped to the specified float id and all the measurements too
    */
  def retrieveMeasurementsAndPath(float_id: String, source: RDD[(String, Iterable[Buoy])]): Ep2DataJsonWrapper = {
    val coordinates = processCoordinatesAndIDsEP2(float_id, source).collect()
    val measurements = source.filter(tuple => tuple._1.equals(float_id))
      .values.flatMap(floatiterable => floatiterable.map(float => (float.PSAL, float.PRES, float.TEMP))).collect().head
    Ep2DataJsonWrapper(PathAndLastMeasurements(measurements._1, measurements._2, measurements._3, coordinates))
  }
}


