package me.simplesoul.circe.decoder

import io.circe.{ACursor, Json, JsonNumber}
import me.simplesoul.circe.encoder.{BaseEncoder, CodingMonkey, Person}
import io.circe.generic.auto._
import io.circe.parser.parse
import me.simplesoul.Helper._

import scala.language.dynamics
import scala.util.{Failure, Success, Try}
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object DynamicJsonDecoder {

  def main(args: Array[String]): Unit = {
    val case1 = """{"A": {"C": "c", "D": "d"}}""".stripMargin
    val result = decodeDynamicly(case1)
    println(result.get)
  }

  val field = "$foo$"

  import BaseEncoder._

  val jsonString = encode(monkey).get.noSpaces

  def decodeDynamicly(jsonString: String): Try[Template] = {
    val template = new Template()
    parse(jsonString).tTry.map {
      case json if json.isArray => parseJsonArray(json, template, field)
      case json if json.isObject => parseJsonObject(json, template, field)
      case json => parseField(json, template, field)
    }
  }

  private def parseField(json: Json, template: Template, key: String): Template = {
    "" match {
      case _ if json.isString => fillTemplate(key, json.asString, template)
      case _ if json.isNumber => fillTemplate(key, json.asNumber, template)
      case _ if json.isBoolean => fillTemplate(key, json.asBoolean, template)
      case _ => fillTemplate(key, Some(""), template)
    }
  }

  private def fillTemplate[T](key: String, value: Option[T], template: Template): Template = {
    value match {
      case Some(v) if v.isInstanceOf[JsonNumber] => {
         v.asInstanceOf[JsonNumber] match {
           case number if number.toInt.nonEmpty => template.updateDynamic(key)(number.toInt.get)
           case number if number.toLong.nonEmpty => template.updateDynamic(key)(number.toLong.get)
           case number => template.updateDynamic(key)(number.toDouble)
         }
      }
      case Some(v) => template.updateDynamic(key)(v)
      case None => template.updateDynamic(key)(null.asInstanceOf[T])
    }
    template
  }

  private def parseJsonObject(json: Json, template: Template, key: String): Template = {
    val cursor = json.hcursor
    cursor.keys.getOrElse(List[String]()).map { k =>
      cursor.downField(k).focus.get match {
        case v if v.isArray => parseJsonArray(v, template, k)
        case v if v.isObject => parseJsonObject(v, template, k)
        case v => parseField(v, template, k)
      }
    }
    template
  }

  private def parseJsonArray(json: Json, template: Template, key: String): Template = {
    json.asArray.get.foreach(parseField(_, template, field))
    template
  }
}


class Template extends Dynamic {

  val int = "Int"
  val double = "Double"
  val string = "String"
  val long = "Long"
  val template = "Template"

  val fields: ListBuffer[(String, Any, String)] = new ListBuffer[(String, Any, String)]()

  def updateDynamic(field: String)(value: Any) = {
    value match {
      case _: Int => fields.append((field, value, int))
      case _: Double => fields.append((field, value, double))
      case _: String => fields.append((field, value, string))
      case _: Long => fields.append((field, value, long))
      case _: Template => fields.append((field, value, template))
      case _ => fields.append((field, value.toString, string))
    }
  }

  def selectDynamic(field: String): Option[(Any, String)] = fields.find(_._1.equals(field)).map(f => f._2 -> f._3)

  def get: Option[(Any, String)] = fields.find(_._1.equals(DynamicJsonDecoder.field)).map(f => f._2 -> f._3)

  override def toString: String = {
    val buffer = new StringBuffer()
    val withoutKey = fields.filter(_._1 == DynamicJsonDecoder.field)
    if(withoutKey.nonEmpty) {
      buffer.append("[")
      withoutKey.foreach {
        case (field, value, tpe) if tpe == string =>
          buffer.append(s""""$value, """")
        case (field, value, tpe) if tpe == template =>
          buffer.append(s"""${template.toString}, """)
        case (field, value, tpe) =>
          buffer.append(s"""$value, """)
      }
      buffer.delete(buffer.length-2, buffer.length)
      buffer.append("]")
    }
    val withKey = fields.filter(_._1 != DynamicJsonDecoder.field)
    withKey.foreach {
      case (field, value, tpe) if tpe == string && field == DynamicJsonDecoder.field =>
        buffer.append(s"""{"$field": "$value"}""")
      case (field, value, tpe) if tpe == template => buffer.append(template.toString)
      case (field, value, tpe) => buffer.append(s"""{"$field": $value}""")
    }
    buffer.toString
  }
}