package com.example.app.model.frontend_endpoints

/**
  * This is a case class to match the exact same structure requested from the frontend
  * Ep1 stands for endpoint 1, because the frontend has 2 endpoints for sending requests
  * @param data the data to be packed in this case class
  */
case class Ep1DataJsonWrapper(data: List[CoordinatesAndID]) {
  def getData: List[CoordinatesAndID] = data
}
