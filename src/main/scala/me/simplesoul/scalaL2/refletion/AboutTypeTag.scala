package me.simplesoul.scalaL2.refletion

import scala.reflect.runtime.universe._
import me.simplesoul.util.Helper._

import scala.reflect.ClassTag
/**
  * @author: SimpleSoul
  * @date: Create at 19-6-27 17:56
  * @description: ${description}
  */
object AboutTypeTag extends App {

  val soul = Stone("soul")

  val glove = Glove(soul)

  implicit class ExtractTags[T](obj: T) {

    def typeTag(implicit tag: TypeTag[T]) = tag

    def classTag(implicit tag: ClassTag[T]) = tag
  }
}

case class Stone(var name: String)

case class Glove[T](declarations: T *)

case class Thanos(glove: Glove[Stone]) {

  def snapFingers = "goodbye world"
}


