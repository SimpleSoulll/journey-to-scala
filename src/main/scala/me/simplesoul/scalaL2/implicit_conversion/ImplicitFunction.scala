package me.simplesoul.scalaL2.implicit_conversion

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-14 18:25
  * @description: docs/about-scala/level 1/隐式转换.md
  */
object ImplicitFunction extends App {

  implicit def stone2Gold(stone: Stone): Gold = stone.toGold

  def buyHouse(gold: Gold): String = { "got a house" }

  val stone = Stone()

  assert(buyHouse(stone) == "got a house")
}

case class Stone() {

  def toGold = Gold()
}

case class Gold()

