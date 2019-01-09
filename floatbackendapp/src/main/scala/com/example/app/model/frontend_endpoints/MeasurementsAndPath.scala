package com.example.app.model.frontend_endpoints

case class MeasurementsAndPath(saltinessValues: Array[Double], pressureValues: Array[Double],
                               temperatureValues: Array[Double], path: Array[Coordinates]) {
  def getSaltinessValues: Array[Double] = saltinessValues
  def getPressureValues: Array[Double] = pressureValues
  def getTemperatureValues: Array[Double] = temperatureValues
  def getPath: Array[Coordinates] = path
}
