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
  //由于隐式类对Cxk的增强,cxk这个实例有了newSong这个字段
  assert(cxk.sing(cxk.newSong) == song)

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
}

class Cxk {

  def sing(song: String): String = song
  def dance: String = "dancing"
  def rap: String = "rapping"
}
