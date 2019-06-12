package com.example.app.storage

import com.mongodb.spark.rdd.MongoRDD
import org.bson.Document

object MongoPipeline {

  trait MElement {
    /**
      * forces conversion to implicit classes that extend MElement
      * @return the same object but converted to MElement
      */
    def melem: MElement = this
  }

  implicit class MString(value: String) extends MElement {
    override def toString: String = s"'$value'"
  }

  implicit class MNumber(value: Int) extends MElement {
    override def toString: String = s"$value"
  }

  case class MArray(elems: MElement*) extends MElement {
    override def toString: String = elems.mkString("[", ", ", "]")
  }

  case class MDoc(elems: (String, MElement)*) extends MElement {
    override def toString: String = elems.map(e => e._1.toString + ": " + e._2.toString).mkString("{", ", ", "}")
  }

  case class Stage(name: String, value: MElement) {
    override def toString: String = s"{$name: $value}"
  }
}


import com.example.app.storage.MongoPipeline._

/**
  * This class represents a mongodb aggregation pipeline consisting of a sequence of stages
  *)
  * @param stages : A sequence of pipeline stages.
  *               See mongodb documentation for more info (https://docs.mongodb.com/manual/reference/operator/aggregation-pipeline)
  */
case class MongoPipeline(stages: Seq[MongoPipeline.Stage]=Seq.empty) {

  def addStage(stage: Stage): MongoPipeline = MongoPipeline(this.stages :+ stage)

  /**
    * Add a match stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_match)
    *
    * @param doc : {$match: doc}
    */
  def Match(doc: MDoc): MongoPipeline = addStage(Stage("$match", doc))

  /**
    * Add a limit stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_limit)
    *
    * @param num : {$limit: num}
    */
  def Limit(num: Int): MongoPipeline = addStage(Stage("$limit", num))

  /**
    * Add a group stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/group/#pipe._S_group)
    *
    * @param doc : {$group: doc}
    */
  def Group(doc: MDoc): MongoPipeline = addStage(Stage("$group", doc))

  /**
    * Add a replaceRoot stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_replaceRoot)
    *
    * @param doc : {$replaceRoot: doc}
    */
  def ReplaceRoot(doc: MDoc): MongoPipeline = addStage(Stage("$replaceRoot", doc))

  /**
    * Add a project stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_project)
    *
    * @param doc : {$project: doc}
    */
  def Project(doc: MDoc): MongoPipeline = addStage(Stage("$project", doc))

  /**
    * Add a sort stage (for more info see https://docs.mongodb.com/manual/reference/operator/aggregation/match/#pipe._S_sort)
    *
    * @param doc : {$project: doc}
    */
  def Sort(doc: MDoc): MongoPipeline = addStage(Stage("$sort", doc))

  /**
    * Run pipeline on MongoRDD
    *
    * @param source MongoRDD source to run pipeline on
    * @return output of pipeline
    */
  def run(source: MongoRDD[Document]): MongoRDD[Document] = {
    val s = stages.map(doc => Document.parse(doc.toString))
    source.withPipeline(s)
  }

}
