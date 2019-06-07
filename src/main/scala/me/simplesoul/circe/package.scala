package me.simplesoul

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-5 11:37
  * @description: ${description}
  */
package object circe {

  case class ParseException(val message: String) extends Exception(message)

}
