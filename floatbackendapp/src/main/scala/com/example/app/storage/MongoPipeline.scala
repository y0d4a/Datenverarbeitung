package com.example.app.storage

import com.example.app.model.frontend_endpoints.CoordinatesAndID
import com.mongodb.spark.rdd.MongoRDD
import org.bson.Document

object MongoPipeline {

  trait MElement

  // MElement inheritors
  case class MString(value: String) extends MElement {
    override def toString: String = s"'$value'"
  }

  case class MInt(value: Int) extends MElement {
    override def toString: String = s"$value"
  }

  case class MArray(elems: MElement*) extends MElement {
    override def toString: String = elems.mkString("[", ", ", "]")
  }

  case class MDoc(elems: (String, MElement)*) extends MElement {
    override def toString: String = elems.map(e => e._1.toString + ": " + e._2.toString).mkString("{", ", ", "}")
  }

  // implicit functions
  object implicits {
    implicit def strToMelem(value: String): MElement = MString(value)

    implicit def intToMelem(value: Int): MElement = MInt(value)

    implicit def arrayToMelem(elems: Seq[MElement]): MElement = MArray(elems: _*)

    implicit def mapToMelem(elems: Seq[(String, MElement)]): MElement = MDoc(elems: _*)

    def elemident(melement: MElement): MElement = melement


    implicit def astrToMelem(value: (String, String)): (String, MElement) = (value._1,  MString(value._2))

    implicit def aintToMelem(value: (String, Int)): (String, MElement) = (value._1,  MInt(value._2))

    implicit def aarrayToMelem(elems: (String, Seq[MElement])): (String, MElement) = (elems._1, MArray(elems._2:_*))

    implicit def amapToMelem(elems: (String, Seq[(String, MElement)])): (String, MElement) = (elems._1,  MDoc(elems._2:_*))
  }

  case class Stage(name: String, value: MElement) {
    override def toString: String = s"{$name: $value}"

  }

}


import com.example.app.storage.MongoPipeline._

/**
  * This class represents a mongodb aggregation pipeline consisting of a sequence of stages
  * )
  *
  * @param stages : A sequence of pipeline stages.
  *               See mongodb documentation for more info (https://docs.mongodb.com/manual/reference/operator/aggregation-pipeline)
  */
case class MongoPipeline(stages: Seq[MongoPipeline.Stage] = Seq.empty) {

  def addStage(stage: Stage): MongoPipeline = MongoPipeline(this.stages :+ stage)

  /**
    * Add a match stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_match)
    *
    * @param doc : {$match: doc}
    */
  def Match(elems: (String, MElement)*): MongoPipeline = addStage(Stage("$match", MDoc(elems:_*)))

  /**
    * Add a limit stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_limit)
    *
    * @param num : {$limit: num}
    */
  def Limit(num: Int): MongoPipeline = addStage(Stage("$limit", MInt(num)))

  /**
    * Add a group stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/group/#pipe._S_group)
    *
    * @param doc : {$group: doc}
    */
  def Group(elems: (String, MElement)*): MongoPipeline = addStage(Stage("$group", MDoc(elems:_*)))

  /**
    * Add a replaceRoot stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_replaceRoot)
    *
    * @param doc : {$replaceRoot: doc}
    */
  def ReplaceRoot(elems: (String, MElement)*): MongoPipeline = addStage(Stage("$replaceRoot", MDoc(elems:_*)))

  /**
    * Add a project stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_project)
    *
    * @param doc : {$project: doc}
    */
  def Project(elems: (String, MElement)*): MongoPipeline = addStage(Stage("$project", MDoc(elems:_*)))

  /**
    * Add a sort stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_sort)
    *
    * @param doc : {$project: doc}
    */
  def Sort(elems: (String, MElement)*): MongoPipeline = addStage(Stage("$sort", MDoc(elems:_*)))

  /**
    * Run pipeline on MongoRDD
    *
    * @param source MongoRDD source to run pipeline on
    * @return output of pipeline
    */
  def run(source: MongoRDD[Document]): MongoRDD[Document] = {
    val s = stages.map(doc => {
      println(doc.toString)
      Document.parse(doc.toString)
    })
    source.withPipeline(s)
  }

}
