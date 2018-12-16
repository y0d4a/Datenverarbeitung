 The first step after receiving the data from the provision team was to create a case class that will later on save all necessary information about a float, or more precisely, about the data of a float for a specific point in time. The case class is quite standard, it takes on the required attributes and also provides get methods to ensure portability of the attributes. It looks like this: 

 ```scala
case class TimedFloat(_id: ObjectId, floatSerialNo: String, longitude: Double, latitude: Double, platformNumber: String,
                      projectName: String, juld: Double, platformType: String, configMissionNumber: Int,
                      cycleNumber: Int, pres: Array[Double], temp: Array[Double], psal: Array[Double])  {

  def getObjectId: ObjectId = _id
  def getFloatSerialNo: String = floatSerialNo
  def getLongitude: Double = longitude
  def getLatitude: Double = latitude
  def getPlatformNumber: String = platformNumber
  def getProjectName: String = projectName
  def getJuld: Double = juld
  def getPlatformType: String = platformType
  def getConfigMissionNumber: Int = configMissionNumber
  def getCycleNumber: Int = cycleNumber
  def getPressure: Array[Double] = pres
  def getTemperature: Array[Double] = temp
  def getPsal: Array[Double] = psal
}
```
The next step was to create a class that will process the data coming from the database and ensure utility methods that will make it easier to handle user requests inside the servlet.

First, we needed to connect to the server where the data is stored, which was quite easy. By using the following dependency 
```javascript 
"org.mongodb.spark" %% "mongo-spark-connector" % "2.3.1"
```
we were able to generate connections effortlessly and read from the database. 

This is the code snippet that achieved this:
```scala
/**
    * The spark session with the needed configuration to write and read from the mongodb databank
    */
  val spark: SparkSession = SparkSession.builder().master("local")
    .appName("MongoSparkConnectorIntro")
    .config("spark.mongodb.input.uri", "mongodb://abteilung6.com/ECCO.ECCO_grouped")
    .config("spark.mongodb.output.uri", "mongodb://abteilung6.com/ECCO.ECCO_grouped")
    .getOrCreate()

  val sc: SparkContext = spark.sparkContext

  /**
    * The dataframe object which accumulates the data from the database
    */
  val df: DataFrame = MongoSpark.load(spark)
```

The object used for retrieving the information from the database was ```df``` of type ```DataFrame```. A ```DataFrame``` works with ```Row``` and ```Column``` objects and provides a schema that can be used for reference on how to access certain fields. Ours looks like this:  

 ```javascript
 |-- _id: string (nullable = true)
 |-- content: array (nullable = true)
 |    |-- element: struct (containsNull = true)
 |    |    |-- _id: struct (nullable = true)
 |    |    |    |-- oid: string (nullable = true)
 |    |    |-- floatSerialNo: string (nullable = true)
 |    |    |-- longitude: double (nullable = true)
 |    |    |-- latitude: double (nullable = true)
 |    |    |-- platformNumber: string (nullable = true)
 |    |    |-- projectName: string (nullable = true)
 |    |    |-- juld: double (nullable = true)
 |    |    |-- platformType: string (nullable = true)
 |    |    |-- configMissionNumber: integer (nullable = true)
 |    |    |-- cycleNumber: integer (nullable = true)
 |    |    |-- pres: array (nullable = true)
 |    |    |    |-- element: double (containsNull = true)
 |    |    |-- temp: array (nullable = true)
 |    |    |    |-- element: double (containsNull = true)
 |    |    |-- psal: array (nullable = true)
 |    |    |    |-- element: double (containsNull = true)
 ```

As you can see in the schema, the ```element``` struct technically has the same structure as our ```TimedFloat``` case class, which is exactly what we want. 
Parsing it to our case class object was achieved with the following function, which saves the float serial number as a key and a list of all ```TimedFloat``` objects mapped to that key and returns a ```Map```: 
```scala
 /**
    * Returns a map containing the floatSerialNumber as a key and all the TimedFloat objects mapped to it
    * @param dataframe the dataframe from which the information should be extracted
    * @return the map
    */
  def retrieveTimedFloats(dataframe: DataFrame): Map[String, List[TimedFloat]] = {
    val unprocessed_data = dataframe.take(dataframe.count().asInstanceOf[Int]).map(row => row.getAs[Seq[Row]]("content"))
    val processed_data = unprocessed_data.flatMap(seq => seq.flatMap(row => List(row)))

    processed_data.foldRight(Map.empty[String, List[TimedFloat]])((row, acc) => acc.updated(row.getAs[String]("floatSerialNo"),
      TimedFloat(
      row.getAs[Double]("longitude"), row.getAs[Double]("latitude"), row.getAs[String]("platformNumber"),
      row.getAs[String]("projectName"), row.getAs[Double]("juld"), row.getAs[String]("platformType"),
      row.getAs[Int]("configMissionNumber"), row.getAs[Int]("cycleNumber"),
      row.getAs[mutable.WrappedArray[Double]]("pres").toArray,
      row.getAs[mutable.WrappedArray[Double]]("temp").toArray,
      row.getAs[mutable.WrappedArray[Double]]("psal").toArray)
        :: acc.getOrElse(row.getAs[String]("floatSerialNo"), List())))
  }
```
