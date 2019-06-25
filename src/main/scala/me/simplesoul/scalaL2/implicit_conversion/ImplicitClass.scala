package me.simplesoul.scalaL2.implicit_conversion

import me.simplesoul.util.Helper._

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-25 19:56
  * @description: ${description}
  */
object ImplicitClass extends App {

  val cxk = new Cxk()

  val song = "beautiful chick"

  assert(cxk.sing(song) == song)
  assert(cxk.dance == "dancing")
  assert(cxk.rap == "rapping")
  //由于隐式类对Cxk的增强,cxk这个实例有了playBall这个方法
  assert(cxk.playBall == "playing basketball")
  assert(cxk.sing(cxk.newSong) == song)
  //由于隐式类对Cxk的增强,cxk这个实例有了newSong这个字段
  assert(cxk.sendLawyersLetter == "sending letter")

  /**
   * @author: Simple Soul
   * @date: 19-06-25 20:10
   * @description: 增强Cxk类,所有Cxk的实例都有一个新的playBall方法和newSong字段.
   * @params: 必须接受Cxk的对象,且只能有这么一个参数
  */
  implicit class AmbassadorCxk(cxk: Cxk) {
    def playBall: String = "playing basketball"
    val newSong = song
  }

  /**
   * @author: Simple Soul
   * @date: 19-06-25 20:19
   * @description: 这是一个更好的实现,AdvancedAmbassadorCxk继承AnyVal,这样sendLawyersLetter会被编译成一个静态方法,
   * 那么意味者每次调用sendLawyersLetter不会有一个新的对象被创建出来.
   * @params: 必须接受Cxk的对象,且只能有这么一个参数
  */
  implicit class AdvancedAmbassadorCxk(val cxk: Cxk) extends AnyVal {
    def sendLawyersLetter: String = "sending letter"
  }
}

class Cxk {

  def sing(song: String): String = song
  def dance: String = "dancing"
  def rap: String = "rapping"
}
