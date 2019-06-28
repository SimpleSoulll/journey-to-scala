package me.simplesoul.topics.dsl.fastparse

import fastparse._
import NoWhitespace._
import fastparse.Parsed.Success
import me.simplesoul.util.Helper._
/**
  * @author: SimpleSoul
  * @date: Create at 19-6-28 16:21
  * @description: ${description}
  */
object HelloParser extends App {

  def parseA[_: P] = P("a")

  val Parsed.Success(value, successIndex) = parse("a", parseA(_))

  value.out

  successIndex.out
}
