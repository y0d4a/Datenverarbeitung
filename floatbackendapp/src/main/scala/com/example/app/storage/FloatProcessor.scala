package com.example.app.storage

import com.example.app.model.TimedFloat
import com.mongodb.spark.MongoSpark
import com.mongodb.spark.sql.fieldTypes.ObjectId
import org.apache.spark.SparkContext
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, Encoders, Row, SparkSession}

import scala.collection.{immutable, mutable}

class FloatProcessor {
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

  /**
    * Returns a map containing the floatSerialnumber as key and all coordinates extracted from the DataFrame
    * and mapped to a specific TimedFloat Object.
    * @param source the map containing our timedfloat objects
    * @return map with coordinates
    */
  def extractCoordinates(source: Map[String, List[TimedFloat]]): Map[String, List[(Double, Double)]] = {
    source.foldRight(Map.empty[String, List[(Double, Double)]])((floats, acc) =>
      acc.updated(floats._1, floats._2.flatMap(float => List((float.getLongitude, float.getLatitude)))))
  }
}

object Main {
  def main(args: Array[String]): Unit = {
    val a: FloatProcessor = new FloatProcessor
    val map = a.retrieveTimedFloats(a.df)
    val coordinates = a.extractCoordinates(map)
    a.df.printSchema()
  }
}
