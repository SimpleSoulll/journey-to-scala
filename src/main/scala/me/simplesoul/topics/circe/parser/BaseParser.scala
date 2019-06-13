package me.simplesoul.topics.circe.parser

import io.circe.{Decoder, Json}
import io.circe.parser.parse
import me.simplesoul.util.Helper._

import scala.util.Try

/**
  * @author: SimpleSoul
  * @date: Create at 19-6-4 18:18
  * @description: Json对象与字符串之间的序列化和反序列化
  */
object BaseParser extends App {

    val jsonString = """ { "A": "a", "B": [1,2,3] } """
    val json = parseString(jsonString)
    // json序列化成字符串
    val jsonStringWithoutSpaces = json.get.noSpaces
    val jsonStringWith4Spaces = json.get.spaces4
    val jsonStringWith2Spaces = json.get.spaces2
    assert(jsonStringWithoutSpaces == """{"A":"a","B":[1,2,3]}""")
    jsonStringWith2Spaces.out
    jsonStringWith4Spaces.out



  /**
   * @author: Simple Soul
   * @date: 19-06-05 11:42
   * @description:
   * @params: 符合json格式要求的字符串
   * @implicits:
   * @return: Json实例
   * @throws: ParsingFailure
   * @notice: 不符合格式要求的json字符串会导致ParseFailure
  */
  def parseString(jsonString: String): Try[Json] = parse(jsonString).tTry
}