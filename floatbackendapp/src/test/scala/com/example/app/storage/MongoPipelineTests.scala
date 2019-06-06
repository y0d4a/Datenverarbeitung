package com.example.app.storage

import com.example.app.MyScalatraServlet
import com.example.app.storage.MongoPipeline.MDoc
import org.scalatra.test.scalatest._

class MongoPipelineTests extends ScalatraFunSuite {

  // run "test" in sbt shell to run these tests
  import MongoPipeline._

  test("Test if MongoPipeline generates correct aggregation pipeline strings") {
    val stages = MongoPipeline()
      .Match(MDoc("abc" -> "123".melem))
      .Limit(4)
      .Group(MDoc("_id" -> "xyz".melem))
      .Project(MDoc("_id" -> 0.melem, "newField" -> MDoc("elem0" -> 0, "elem1" -> "1", "elem2" -> "2")))
      .ReplaceRoot(MDoc("newRoot" -> "$newField".melem)).stages

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
