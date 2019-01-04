package com.example.app.model.frontend_endpoints

case class MeasurementsAndPath(saltinessValues: Array[Array[Double]], pressureValues: Array[Array[Double]],
                               temperatureValues: Array[Array[Double]], path: Array[Coordinates]) {
  def getSaltinessValues: Array[Array[Double]] = saltinessValues
  def getPressureValues: Array[Array[Double]] = pressureValues
  def getTemperatureValues: Array[Array[Double]] = temperatureValues
  def getPath: Array[Coordinates] = path
}
