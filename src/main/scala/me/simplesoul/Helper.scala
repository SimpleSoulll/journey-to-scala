package me.simplesoul

import scala.annotation.StaticAnnotation
import scala.util.{Failure, Success, Try}

/**
  * @author: SimpleSoul
  * @date: Create at 19-5-31 18:51
  * @description: ${description}
  */
object Helper {

  case class Trap(description: String) extends StaticAnnotation

  case class Warn() extends StaticAnnotation

  /**
   * @author: Simple Soul
   * @date: 19-05-31 19:20
   * @description: transform an Enther[Exception, T] to Try[T]
   * @params: Either[Exception, T]
  */
  implicit class EitherToTry[T](either: Either[Exception, T]) {
    def tTry: Try[T] = either.fold(error => Failure(error),t => Success(t))
  }

  implicit class PrintOut(content: Any) {
    def out = println(content.toString)
  }
}

