package com.example.app.model.frontend_endpoints

/**
  * Maps this: ITS ONLY FOR ONE FLOAT ! The float gets identified by the id that the frontend already received in endpoint1
  *{
  *  "data": {
  *     "saltinessValues": [...],
  *     "pressureValues": [...],
  *     "tempValues": [...]
  *   }
  *}
   */
case class Ep3DataJsonWrapper(data: Measurements) {
  def getData: Measurements = data
}
