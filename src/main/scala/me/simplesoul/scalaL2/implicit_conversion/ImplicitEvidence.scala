package me.simplesoul.scalaL2.implicit_conversion

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-21 10:26
  * @description: ${description}
  */
object ImplicitEvidence extends App {

  implicit object NonEmptyCard extends Checkable[CreditCard] {

    override def isEmpty(obj: CreditCard): Boolean = obj.money <= 0
  }

  def validate[T](obj: T)(implicit ev: Checkable[T]): Option[String] = {
    if(ev.isEmpty(obj)) Some(s"$obj is empty") else None
  }

  def validate0[T: Checkable](obj: T): Option[String] = {
    val checker = implicitly[Checkable[T]]
    if(checker.isEmpty(obj)) Some(s"$obj is empty") else None
  }
}

trait Checkable[T] {

  def isEmpty(obj: T): Boolean
}

case class CreditCard(money: Double)

