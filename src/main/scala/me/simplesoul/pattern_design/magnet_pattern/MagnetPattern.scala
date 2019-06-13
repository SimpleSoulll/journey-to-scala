package me.simplesoul.pattern_design.magnet_pattern
<<<<<<< HEAD
import me.simplesoul.pattern_design.magnet_pattern.MagnetImpl.Magnet

import scala.reflect.runtime.universe._

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-10 11:29
  * @description: 磁铁模式示例 @ docs/pattern-design/磁铁模式.md
  */
object MagnetPattern {

  def main(args: Array[String]): Unit = {

    assert(NormalImpl.doubling("abc") == Some("abcabc"))
    assert(NormalImpl.doubling(1) == Some(2))

    import MagnetImpl.String2Magnet
    assert(MagnetImpl.doubling("abc") == "abcabc")

    // 扩展Magnet.doubling能够处理的类型
    implicit def Int2Magnet(value: Int): Magnet = new Magnet {
      override type Result = Int
      override def double: Result = value + value
    }
    assert(MagnetImpl.doubling(1) == 2)
  }
}

/**
  * @author: Simple Soul
  * @date: 19-06-10 11:35
  * @description: 企图用方法重载实现
  * @params: 如果没有反省擦除导致的约束,只要重载的足够完备,就可以处理一切对象
  * @return: double之后的结果
  * @notice: 由于泛型擦除,不可能重载的完备
  */
object SBImpl {

  def doubling(num: Int) = num + num // this is ok

  def doubling(str: String) = str + str // also ok

  def double(nums: List[Int]) = nums.map(_ * 2) // ok as well

//  def double(strs: List[String]) = strs.map(s => s + s) // 无法编译, 泛型擦除导致与上个函数重复定义
}

object NormalImpl {

  /**
    * @author: Simple Soul
    * @date: 19-06-10 11:35
    * @description: 常规方式实现的doubling
    * @params: 任何对象
    * @return: double之后的结果
    * @notice: 扩展doubling能够处理的类型的方式就是在match中增加匹配其他类型的case.
    *          显然这种扩展方式很不友好,如果这个doubling时被封装起来的,那岂不是得动态代理,然后在doubling内织人新逻辑...
    */
  def doubling[T: TypeTag](obj: T): Option[T] = {
    typeOf[T] match {
      case tpe if tpe =:= typeOf[String] => val instance = obj.asInstanceOf[String]; Some((instance + instance).asInstanceOf[T])
      case tpe if tpe =:= typeOf[Int] => val instance = obj.asInstanceOf[Int]; Some((instance + instance).asInstanceOf[T])
      case _ => None
    }
  }
}

object MagnetImpl {

  /**
    * @author: Simple Soul
    * @date: 19-06-10 11:43
    * @description: 磁铁模式实现的doubling
    * @params: 任意混入了Magnet特质对象的实例
    * @return: double之后的结果
    * @notice: 具有更好的可扩展性
    */
  def doubling(obj: Magnet): obj.Result = obj.double

  implicit def String2Magnet(str: String): Magnet = { // 返回类型必须给定.否则隐士函数无法正确匹配!
    new Magnet {
      override type Result = String
      override def double: Result = str + str
    }
  }

  implicit def stringList2Magnet[String](strs: List[String]): Magnet = {
    new Magnet {
      override type Result = List[String]

      override def double: List[String] = strs ++ strs
    }
  }

  implicit def stringList2Magnet[Int](numbs: List[Int]): Magnet = {
    new Magnet {
      override type Result = List[Int]

      override def double: List[Int] = numbs ++ numbs
    }
  }

  trait Magnet {
    type Result
    def double: Result
  }
}


=======

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-9 14:39
  * @description: 磁铁模式实现的输入校验
  */
object MagnetPattern {

}
>>>>>>> b125f0042d7ad0cf7279bd5e448d8b7d888be757
