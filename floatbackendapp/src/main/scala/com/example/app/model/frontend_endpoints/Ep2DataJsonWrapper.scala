package com.example.app.model.frontend_endpoints

case class Ep2DataJsonWrapper(data: MeasurementsAndPath) {
  def getData: MeasurementsAndPath = data
}
