package me.simplesoul.scalaL2.implicit_conversion

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-21 10:26
  * @description: ${description}
  */
object ImplicitEvidence extends App {

}

trait Empty {

  def checkEmpty[T](obj: T): Boolean
}

case class Pocket(money: Double)