package com.example.app.model.frontend_endpoints

/**
  * Maps this: ITS ONLY FOR ONE FLOAT ! The float gets identified by the id that the frontend already received in endpoint1
  * {
  *   "saltinessValues": [...],
  *   "pressureValues": [...],
  *   "tempValues": [...],
  *   "path": [
  *     {
  *       "longitude": 123,
  *       "latitude: 124,
  *     }
  *     {
  *       "longitude": 125,
  *       "latitude: 126
  *     }
  *   ]
  * }
  * @param saltinessValues
  * @param pressureValues
  * @param temperatureValues
  * @param path
  */
case class PathAndLastMeasurements(saltinessValues: Array[Double], pressureValues: Array[Double],
                                   temperatureValues: Array[Double], path: Array[Coordinates]) {
  def getSaltinessValues: Array[Double] = saltinessValues
  def getPressureValues: Array[Double] = pressureValues
  def getTemperatureValues: Array[Double] = temperatureValues
  def getPath: Array[Coordinates] = path
}
