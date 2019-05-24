package com.example.app.storage
import com.example.app.model.frontend_endpoints._
import com.example.app.model.{Buoy, frontend_endpoints}
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.rdd.MongoRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, _}
import org.bson.Document
import org.bson.BsonArray

class BuoyProcessor {

  //System.setProperty("hadoop.home.dir", "C:\\\\hadoop")

  /**
    * This object connects to the database and initializes itself with the configurations specified
    * spark.mongodb.input.uri means that we are can write to the database
    * spark.mongodb.output.uri means that we can read from the database
    */
  val sparkSession: SparkSession = SparkSession.builder().master("local[*]")
    .appName("BuoyREST_Interface")
    //.config("spark.mongodb.input.uri", "mongodb://ecco:kd23.S.W@hadoop05.f4.htw-berlin.de:27020/ecco.buoy")
    //.config("spark.mongodb.output.uri", "mongodb://ecco:kd23.S.W@hadoop05.f4.htw-berlin.de:27020/ecco.buoy")
    .config("spark.mongodb.input.uri", "mongodb://ecco:kd23.S.W@localhost:27020/ecco.buoy")
    .config("spark.mongodb.output.uri", "mongodb://ecco:kd23.S.W@localhost:27020/ecco.buoy")
    .config("spark.ui.port", "4444")
    .getOrCreate()
  // TODO: outsource credentials to environment variables

  /**
    * This import statement is needed to convert the data coming from mongodb to our case class, which will ensure a more
    * readable and robust code structure
    */
  import sparkSession.implicits._

  /**
    * The dataset containing the buoyserialnumber as a key and all buoys mapped to that key as value
    */
  val source: MongoRDD[Document] = MongoSpark.load(sparkSession.sparkContext)

  private def pipeline(docs: Seq[String]): MongoRDD[Document] = {
    source.withPipeline(docs.map(Document.parse))
  }

  /**
    * Here we save the coordinates for the specified buoy id AND we store the measurements of the buoy with the specified
    * buoy id, by filtering the buoys in the databank and finding the ones that match the given id. Then we take all the
    * measurement arrays mapped to that buoy and we save them together with the coordinates inside the object.
    * Then we wrap the object inside the Ep2DataJsonWrapper, which is another object, because thats how the frontend
    * wanted to receive the data
    * @param buoyId the buoy id
    * @return all coordinates mapped to the specified buoy id and all the measurements too
    */
  def retrieveMeasurementsAndPath(buoyId: String): Ep2DataJsonWrapper = {
    val measurements = pipeline(Seq(
      "{$match: { floatSerialNo : '" + buoyId + "' }}",
      "{$limit: 1}"
    )).toDS[Buoy].collect()(0)

    val coordinates = pipeline(Seq(
      "{$match: { floatSerialNo: '" + buoyId + "' }}",
      //"{$group: {_id: 'coords', coord: {$push: {'longitude': '$longitude', 'latitude': '$latitude'}}}}",
      //"{$replaceRoot: { newRoot: '$coords' }}"
      "{$project: {'longitude': 1, 'latitude': 1}}"
    )).toDS[Coordinates].collect()
    val result = MeasurementsAndPath(measurements.PSAL, measurements.PRES, measurements.TEMP, coordinates)
    /*
    val result = rdd.toDS[Buoy].collect().map(m => MeasurementsAndPath(m.PSAL, m.PRES, m.TEMP, Array(Coordinates(m.longitude, m.latitude))))
      .reduce((a, b) => MeasurementsAndPath(
        a.saltinessValues ++ b.saltinessValues,
        a.pressureValues ++ b.pressureValues,
        a.temperatureValues ++ b.temperatureValues,
        a.path ++ b.path
      ))
      */
    Ep2DataJsonWrapper(result)
  }
}



