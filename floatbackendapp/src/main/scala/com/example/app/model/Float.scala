package com.example.app.model
/*
case class Float(_id: String, content: Seq[TimedFloat]) {
  def get_Id: String = _id
  def getContent: Seq[TimedFloat] = content
}
*/

case class Float(PRES: Array[Double], TEMP: Array[Double], PSAL: Array[Double], floatSerialNo: String,
                 longitude: Double, latitude: Double, platformNumber: String, projectName: String,
                 juld: Double, platformType: String, configMissionNumber: Int, cycleNumber: Int)