package com.example.app.storage

import com.example.app.MyScalatraServlet
import com.example.app.storage.MongoPipeline.MDoc
import org.scalatra.test.scalatest._

class MongoPipelineTests extends ScalatraFunSuite {

  // run "test" in sbt shell to run these tests
  import MongoPipeline._
  import MongoPipeline.implicits._


  test("Test if MongoPipeline generates correct aggregation pipeline strings") {
    val stages = MongoPipeline()
      .Match("abc" -> "123")
      .Limit(4)
      .Group("_id" -> "xyz")
      .Project("_id" -> 0, "newField" -> MDoc("elem0" -> 0, "elem1" -> "1", "elem2" -> "2"))
      .ReplaceRoot("newRoot" -> "$newField").stages

    val correctStrs = Seq(
      "{$match: {abc: '123'}}",
      "{$limit: 4}",
      "{$group: {_id: 'xyz'}}",
      "{$project: {_id: 0, newField: {elem0: 0, elem1: '1', elem2: '2'}}}",
      "{$replaceRoot: {newRoot: '$newField'}}"
    )
    for ((stage, str) <- stages.zip(correctStrs)) assert(stage.toString.equals(str))
  }

}
