package com.example.app.model.frontend_endpoints

/**
  * This is a case class to match the exact same structure requested from the frontend
  * Ep1 stands for endpoint 1, because the frontend has 2 endpoints for sending requests
  * Maps this:
  *
  * {
  *   "data": [
  *     {
  *       "id" blahblah
  *       "coordinates": {
  *         "longitude": 123
  *         "latitude": 124
  *       }
  *     },
  *     {
  *       "id" blahblah2
  *       "coordinates": {
  *         "longitude": 125
  *         "longitude": 126
  *       }
  *     },
  *     ...
  *     ...
  *   ]
  * }
  * @param data the data to be packed in this case class
  */
case class Ep1DataJsonWrapper(data: Array[CoordinatesAndID]) {
}
