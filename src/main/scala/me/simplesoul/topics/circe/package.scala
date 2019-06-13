package me.simplesoul

import io.circe.Encoder
import io.circe.generic.JsonCodec
import me.simplesoul.circe.HairAmount.HairAmount

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-5 11:37
  * @description: ${description}
  */
package object circe {

  @JsonCodec sealed trait Codeable

  case class ParseException(val message: String) extends Exception(message)

  object HairAmount extends Enumeration {

    type HairAmount = Value

    val rich, soso, poor, empty = Value

    implicit val encoder: Encoder[Value] = Encoder.enumEncoder(HairAmount)
  }

  case class Person(name: String, age: Int) extends Codeable

  case class AI(name: String) extends Codeable

  case class CodingMonkey(languageUse: String, person: Codeable)

  case class CodingMonkeyWithHair(languageUse: String, person: Person, hairAmount: HairAmount)

  case class CodingMonkeyWithSkilledLanguage[T](skill: T)

  case class Scala(name: String = "Scala")

  case class Haskell[T](name: String, whatever: T)

  case class Whatever[V](foo: String, something: V)
}
