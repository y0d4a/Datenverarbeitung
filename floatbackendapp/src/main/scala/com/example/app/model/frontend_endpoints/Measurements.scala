package com.example.app.model.frontend_endpoints

/**
  * Maps this: ITS ONLY FOR ONE FLOAT ! The float gets identified by the id that the frontend already received in endpoint1
  * {
  *   "saltinessValues": [...],
  *   "pressureValues": [...],
  *   "tempValues": [...]
  * }
  * @param saltinessValues
  * @param pressureValues
  * @param temperatureValues
  */
case class Measurements(saltinessValues: Array[Double], pressureValues: Array[Double],
                                   temperatureValues: Array[Double]) {
  def getSaltinessValues: Array[Double] = saltinessValues
  def getPressureValues: Array[Double] = pressureValues
  def getTemperatureValues: Array[Double] = temperatureValues
}
