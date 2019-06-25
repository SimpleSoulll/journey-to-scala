//package me.simplesoul.util.Validator
//
//import scala.annotation.StaticAnnotation
//import scala.reflect.ClassTag
//import scala.reflect.runtime.universe
//import scala.reflect.runtime.universe._
//import me.simplesoul.util.Helper._
//
//import scala.collection.mutable.ListBuffer
//
///**
//  * @author: SimpleSoul
//  * @date: Create at 19-6-9 14:39
//  * @description: 输入参数校验器
//  */
//object ValidatorImpl extends App {
//
////  val person = new Person("abc", 27, Experience("Scala", 3))
////
////  Validator.validate(person).out
//
//  val between = Between(1,3)
//
//  val empty = NonEmpty()
//
//  getAnotherInstance(empty).asInstanceOf[NonEmpty].validate("xx", "").out
//
//  def reflectValidator[T: TypeTag](obj: T)(implicit tag: ClassTag[T]): MethodMirror = {
//    val validatorSymbol = typeOf[T].member(TermName("validate")).asMethod
//    mirror.reflect(obj).reflectMethod(validatorSymbol)
//  }
//
//  def getAnotherInstance[T: TypeTag : ClassTag](obj: T) = {
//    val tpe = typeOf[T]
//    val lowerTerm = tpe.member(TermName("lower")).asTerm
//    val upperTerm = tpe.member(TermName("upper")).asTerm
//    val orderTerm = tpe.member(TermName("order")).asTerm
//    val lower = mirror.reflect(obj).reflectField(lowerTerm).get
//    val upper = mirror.reflect(obj).reflectField(upperTerm).get
//    val order = mirror.reflect(obj).reflectField(orderTerm).get
//    val constructorSymbol = tpe.member(termNames.CONSTRUCTOR).alternatives.head.asMethod
//    mirror.reflectClass(tpe.typeSymbol.asClass).reflectConstructor(constructorSymbol).apply(lower, upper, order)
//  }
//}
//
//object Validator {
//
//  def validate[T: TypeTag](obj: T)(implicit tag: ClassTag[T]): Option[String] = {
//    val results = ListBuffer[Option[String]]()
//    universe.typeOf[T].decl(termNames.CONSTRUCTOR).asMethod.paramLists.head.map(_.asTerm).collectFirst {
//      case field if validateField(obj, field).nonEmpty => validateField(obj, field)
//    }.getOrElse(None)
//  }
//
//  def validateField[T: TypeTag](obj: T, field: TermSymbol)(implicit tag: ClassTag[T]): Option[String] = {
//    field.annotations.filter(_.tree.tpe <:< typeOf[ValidateAnnotation]).collectFirst { case annotation =>
//      val validatorSymbol = annotation.tree.tpe.member(TermName("validate")).asMethod
//      val tpe = annotation.tree.tpe
//      val constructor = tpe.decl(termNames.CONSTRUCTOR).asMethod
//      val args = annotation.tree.children.tail.map(_.productElement(0).asInstanceOf[Constant].value.asInstanceOf[T])
////      val otherArgs = /* generate here */
//      val validator = mirror.reflect(mirror.reflectClass(tpe.typeSymbol.asClass).reflectConstructor(constructor)
////        .apply((args ::: otherArgs.getOrElse(Nil)): _*)).instance // pass here
//      val validatorMirror = mirror.reflect(validator).reflectMethod(validatorSymbol)
//      val value = getFieldValue(obj, field)
//      validatorMirror.apply(field.name.decoded, value).asInstanceOf[Option[String]]
//    }.getOrElse(None)
//  }
//
//  def getFieldValue[T: TypeTag](obj: T, field: TermSymbol)(implicit tag: ClassTag[T]): Any = {
//    val s = typeOf[T].declaration(TermName(field.name.decoded))
//    mirror.reflect(obj).reflectField(s.asTerm).get
//  }
//}
//
//
//
///**
//  * @author: Simple Soul
//  * @date: 19-06-09 15:00
//  * @description: 用于校验输入参数上下界的注解
//  * @params: 下界和上界
//  * @notice: 需要提供Ordering
//  */
//case class Between[T](lower: T, upper: T)(implicit order: Ordering[T]) extends ValidateAnnotation {
//
//  def validate(name: String, value: T): Option[String] = {
//    if(order.lteq(value, lower) || order.gteq(value, upper)) Some(s"$name is not between $lower and $upper") else None
//  }
//}
//
///**
//  * @author: Simple Soul
//  * @date: 19-06-09 15:01
//  * @description: 用于校验输入参数是否为空的注解
//  * @notice: 输入参数必须实现了isEmpty方法
//  */
//case class NonEmpty() extends ValidateAnnotation {
//
//  def validate(name: String, value: Any): Option[String] = {
//    "validate nonEmpty".out
//    scala.util.Try(mirror.classSymbol(value.getClass).toType.member(TermName("isEmpty")).asMethod).map { isEmptySymbol =>
//      if(!mirror.reflect(value)
//          .reflectMethod(isEmptySymbol).apply().asInstanceOf[Boolean]) None
//      else Some(s"$name is empty")
//    }.recover { case _: ScalaReflectionException =>
//      Some(s"$name.isEmpty is not defined")}.get
//  }
//}
//
///**
//  * @author: Simple Soul
//  * @date: 19-06-09 15:01
//  * @description: Validate注解的实例，说明的该实例有需要校验的字段, 检验时会继续向下校验
//  */
//case class Validate() extends ValidateAnnotation {
//  def validate(name: String, obj: Any): Option[String] = {
//    None
//  }
//}
//
//trait ValidateAnnotation extends StaticAnnotation
//
//case class Person(@NonEmpty() val name: String, @Between(4, 5) age: Int, @Validate() xperience: Experience)
//
//case class Experience(language: String, @Between(3, 5) time: Int)
//
//
//
