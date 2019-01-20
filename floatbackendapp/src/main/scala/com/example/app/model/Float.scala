package com.example.app.model

/**
  * Case class to transform a whole data entry from the database to a scala object
  * @param PRES
  * @param TEMP
  * @param PSAL
  * @param floatSerialNo
  * @param longitude
  * @param latitude
  * @param platformNumber
  * @param projectName
  * @param juld
  * @param platformType
  * @param configMissionNumber
  * @param cycleNumber
  */
case class Float(PRES: Array[Double], TEMP: Array[Double], PSAL: Array[Double], floatSerialNo: String,
                 longitude: Double, latitude: Double, platformNumber: String, projectName: String,
                 juld: Double, platformType: String, configMissionNumber: Int, cycleNumber: Int)