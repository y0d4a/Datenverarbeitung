package com.example.app.model.frontend_endpoints

case class CoordinatesAndID(id: String, coordinates: Coordinates) {
  def getId: String = id
  def getCoordinates: Coordinates = coordinates
}
