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

  val pocket = Pocket(10)

//  2 * pocket // compile error. Int * 这个表达式不能接收一个Pocket对象

  implicit def pocket2Double(pocket: Pocket) = pocket.money

  assert(2 * pocket == 20) // Int * 这个表达式不能接收一个Pocket对象,但可以接收一个Double对象, 编译器会利用pocket2Double将pocket转换为Double再计算
}

case class Pocket(money: Double)

case class Stone() {

  def toGold = Gold()
}

case class Gold()

