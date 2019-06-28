package me.simplesoul.scalaL2.refletion
import scala.reflect.runtime.universe._
import me.simplesoul.util.Helper._

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-19 18:39
  * @description: ${description}
  */
object AboutSymbol extends App {

  val thanos = Thanos(Glove(
    Stone("power"),
    Stone("time"),
    Stone("space"),
    Stone("soul"),
    Stone("reality"),
    Stone("mind")
  ))

  val tag = implicitly[TypeTag[Thanos]]
}

