package me.simplesoul.topics.circe.encoder

import java.io.File

import io.circe.{Encoder, Json}
import io.circe.syntax._
import io.circe.generic.auto._
import me.simplesoul.circe._

import scala.util.Try

/**
  * @author: SimpleSoul
  * @date: Create at 19-5-31 17:50
  * @description: 对象序列化
  */
object BaseEncoder extends App {

  val person = Person("simplesoul", 18)

  val monkey = CodingMonkey("scala", person)

  val monkeyWithHair = CodingMonkeyWithHair("scala", person, HairAmount.rich)

  val monkeyWithScala = CodingMonkeyWithSkilledLanguage(new Scala())

  val monkeyWithHaskell = CodingMonkeyWithSkilledLanguage(new Haskell("Hashkell", Whatever("hello world", person)))

  val file = new File("xxx")
  implicit val encodeFile: Encoder[File] = file => Map("name" -> file.getName).asJson

  encode(monkey).map(json => assert(json.noSpaces == """{"languageUse":"scala","person":{"name":"simplesoul","age":18}}""")).recover {
    case ex: Exception => ex.printStackTrace
  }

  encode(monkeyWithHair).map(json => assert(json.noSpaces == """{"languageUse":"scala","person":{"name":"simplesoul","age":18},"hairAmount":"rich"}""")).recover {
    case ex: Exception => ex.printStackTrace
  }

  encode(monkeyWithScala).map(json => assert(json.noSpaces == """{"skill":{"name":"Scala"}}""")).recover {
    case ex: Exception => ex.printStackTrace
  }

  encode(monkeyWithHaskell).map(json => assert(json.noSpaces == """{"skill":{"name":"Hashkell","whatever":{"foo":"hello world","something":{"name":"simplesoul","age":18}}}""")).recover {
    case ex: Exception => ex.printStackTrace
  }

  encode(file).map(json => assert(json.noSpaces == """{"name":"xxx"}""")).recover {
    case ex: Exception => ex.printStackTrace
  }

  /**
    * @author: Simple Soul
    * @date: 19-05-31 17:51
    * @description: 将实例序列化成json
    * @params: 一个对象实例
    * @implicits: encoder: default encoder for T
    * @return: 如果序列化成功则返回json对象
    * @throws:
    * @notice: T不能涉及无法序列化的对象(例如Any或函数).
  */
  def encode[T](obj: T)(implicit encoder: Encoder[T]): Try[Json] = Try(obj.asJson)

}



