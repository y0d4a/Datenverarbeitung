package com.example.app.model

case class Float(_id: String, content: Seq[TimedFloat]) {
  def get_Id: String = _id
  def getContent: Seq[TimedFloat] = content
}
