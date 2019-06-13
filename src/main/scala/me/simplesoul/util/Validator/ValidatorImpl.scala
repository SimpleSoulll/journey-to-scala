package me.simplesoul.util.Validator

import scala.annotation.StaticAnnotation
import scala.collection.mutable.ListBuffer
import scala.reflect.ClassTag
import scala.reflect.runtime.universe
import scala.reflect.runtime.universe._
import me.simplesoul.util.Helper._

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-9 14:39
  * @description: 输入参数校验器
  */
object ValidatorImpl extends App {

  val person = new Person("", 27, Experience("Scala", 3))

  Validator.validate(person).get.out

//  val annotation = universe.typeOf[Person].member(termNames.CONSTRUCTOR).asMethod.paramLists.head.map(_.asTerm).find(_.name.decoded == "age").get.annotations.head
//  val tpe = annotation.tree.tpe
//  val constructor = tpe.decl(termNames.CONSTRUCTOR).asMethod
//
//  val params = annotation.tree.children.tail.map(_.productElement(0).asInstanceOf[Constant].value.asInstanceOf[Int])
//
//  params.out
//
//  val s = mirror.reflectClass(tpe.typeSymbol.asClass).reflectConstructor(constructor).apply(params: _*)

}

trait ValidateAnnotation extends StaticAnnotation

object Validator {

  def validate[T: TypeTag](obj: T)(implicit tag: ClassTag[T]) = {
    universe.typeOf[T].decl(termNames.CONSTRUCTOR).asMethod.paramLists.head.map(_.asTerm).collectFirst { case field =>
      field.annotations.filter(_.tree.tpe <:< typeOf[ValidateAnnotation]).collectFirst { case annotation =>
        obj.getClass.getDeclaredField(field.name.toString).get(obj).out
        companion[Magnet](annotation.tree.tpe.typeSymbol.fullName).validate(field.name.toString, obj.getClass.getDeclaredField(field.name.toString).get(obj))
      }.getOrElse(None)
    }.getOrElse(None)

//    universe.typeOf[T].decl(termNames.CONSTRUCTOR).asMethod.paramLists.head.map(_.asTerm).collectFirst { case field =>
//      field.annotations.filter(_.tree.tpe <:< typeOf[ValidateAnnotation]).collectFirst { case annotation =>
//        val validatorSymbol = annotation.tree.tpe.member(TermName("validate")).asMethod
//        val tpe = annotation.tree.tpe
//        val constructor = tpe.decl(termNames.CONSTRUCTOR).asMethod
//        val args = annotation.tree.children.tail.map(_.productElement(0).asInstanceOf[Constant].value.asInstanceOf[T])
//        val validator = mirror.reflect(mirror.reflectClass(tpe.typeSymbol.asClass).
//            reflectConstructor(constructor).apply(args: _*)).instance
//        mirror.reflect(validator).reflectMethod(validatorSymbol).apply(field.name.decodedName.toString, mirror.reflect(obj).reflectField(field).get)
//      }.getOrElse(None)
//    }.getOrElse(None)
  }

  def companion[Magnet](name : String)(implicit man: Manifest[Magnet]): Magnet =
    Class.forName(name + "$").getField("MODULE$").get(man.erasure).asInstanceOf[Magnet]

//  def validate[T: TypeTag](obj: T): Option[String] = {
//    nonValidatorAnnotationCache.find(_.equals(universe.typeOf[T])) match {
//      case Some(_) => None
//      case _ => { // 没有在无校验器列表中被缓存，则有两种情况：case1:在检验器列表中被缓存，需要被校验;case2: 第一次被校验，无缓存信息
//        validatorAnnotationCache.filter(_.clazz.equals(typeOf[T])) match {
//            case cachedFields if cachedFields.nonEmpty => { // case1, 开始校验
//              cachedFields.collectFirst { case cachedField =>
//                cachedField.validators.collectFirst {
//                  case validator if {println(validator);validator.apply(cachedField.field.name.decodedName.toString, obj).asInstanceOf[Option[String]].nonEmpty} =>
//                    validator.apply(cachedField.field.name.decodedName.toString, obj).asInstanceOf[Option[String]]
//                }.getOrElse(None)
//              }.getOrElse(None)
//            }
//            case cachedFields if cachedFields.isEmpty => { // case2, 扫描T的校验器，并缓存，然后再调用validator
//              cacheAnnotatedField(obj)
//              validate(obj)
//            }
//            case _ => None
//          }
//        }
//      }
//    }
}



/**
  * @author: Simple Soul
  * @date: 19-06-09 15:00
  * @description: 用于校验输入参数上下界的注解
  * @params: 下界和上界
  * @notice: 被校验的参数必须实现了Ordered
  */
case class Between[T](lower: T, upper: T) extends ValidateAnnotation {

  def validate(name: String, value: Ordered[T]): Option[String] = {
    if(value < lower || value > upper) Some(s"$name is not between $lower and $upper") else None
  }
}

/**
  * @author: Simple Soul
  * @date: 19-06-09 15:01
  * @description: 用于校验输入参数是否为空的注解
  * @notice: 输入参数必须实现了isEmpty方法
  */
case class NonEmpty() extends ValidateAnnotation {

  def validate[T](name: String, value: T): Option[String] = {
    value.out
//    value.getClass.getDeclaredMethod("isEmpty").setAccessible(true)
//    value.getClass.getDeclaredMethod("isEmpty").invoke(value)

//    scala.util.Try(mirror.classSymbol(value.getClass).toType.member(TermName("isEmpty")).asMethod).map { isEmptySymbol =>
//      if(!mirror.reflect(value)
//          .reflectMethod(isEmptySymbol).apply().asInstanceOf[Boolean]) None
//      else Some(s"$name is empty")
//    }.recover { case _: ScalaReflectionException =>
//      Some(s"$name.isEmpty is not defined")}.get
    None
  }
}

trait Magnet {

  def validate[T: TypeTag](name: String, obj: T): Option[String]
}

object NonEmpty extends Magnet {

  override def validate[T: TypeTag](name: String, obj: T): Option[String] = {
    typeOf[T] match {
      case tpe if tpe =:= typeOf[String] => if(obj.asInstanceOf[String].isEmpty) Some(s"$name is Empty") else None
      case tpe if tpe =:= typeOf[Iterator[_]] => if(obj.asInstanceOf[Iterator[_]].isEmpty) Some(s"$name is Empty") else None
      case tpe if tpe =:= typeOf[Option[_]] => if(obj.asInstanceOf[Option[_]].isEmpty) Some(s"$name is Empty") else None
      case tpe => Some(s"empty check for ${tpe.typeSymbol.name.decodedName.toString} is not support")
    }
  }
}

/**
  * @author: Simple Soul
  * @date: 19-06-09 15:01
  * @description: Validate注解的实例，说明的该实例有需要校验的字段, 检验时会继续向下校验
  */
case class Validate() extends ValidateAnnotation

case class Person(@NonEmpty() val name: String, @Between(4, 5) age: Int, experience: Experience)

case class Experience(language: String, @Between(3, 5) time: Int)



