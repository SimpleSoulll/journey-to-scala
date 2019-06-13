circe是scala处理json比较成熟的工具之一，scala处理json常用的工具还有json4s。

circe依赖([maven]):
"io.circe" %% "circe-core" % "0.10.0",
"io.circe" %% "circe-generic" % "0.10.0",
"io.circe" %% "circe-parser" % "0.10.0",
"io.circe" %% "circe-optics" % "0.10.0",

json处理的实质就是序列化和反序列化，将特定对象序列化成Json对象的操作可以称为json序列化，将Json对象反序列化成特定对象的操作可以被成为Json反序列化，在circe中这两个操作分别称为Decode和Encode。更一般地，有时需要将特定对象序列化成Json对象，然后序列化成字符串，或将字符串反序列化成Json对象，然后将Json对象反序列化成特定对象。

---
### Json <=> String Json与String的序列化和反序列化
```
    val jsonString = """ { "A": "a", "B": [1,2,3] } """
    
    // String => Json
    val json = parseString(jsonString)
    
    // json => String
    val jsonStringWithoutSpaces = json.get.noSpaces
    val jsonStringWith4Spaces = json.get.spaces4
    val jsonStringWith2Spaces = json.get.spaces2
    
    assert(jsonStringWithoutSpaces == """{"A":"a","B":[1,2,3]}""")
    assert(jsonStringWith2Spaces == """{
                               |  "A" : "a",
                               |  "B" : [
                               |    1,
                               |    2,
                               |    3
                               |  ]
                               |}""".stripMargin)
    assert(jsonStringWith4Spaces == """{
                                      |    "A" : "a",
                                      |    "B" : [
                                      |        1,
                                      |        2,
                                      |        3
                                      |    ]
                                      |}""".stripMargin)`
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
```
---
在说明decode和encode之前先明确以下几点,以下所说的序列化都是指Json序列化：

(1) decode和encode的实质是以Json形式进行对象序列化或反序列化，因此Java对象序列化的概念和注解(如@transient)适用于decode和encode过程；在对象序列化中"**一个对象能够被序列化的前提是:(1)它的所有成员能够序列化；(2)其父类能够序列化**"的概念适用于Json序列化；

(2) decode过程依赖隐式参数Decoder，encode的过程依赖隐式参数Encoder，**circe为所有基类自动实现了Encoder和Decoder**，也就是说基础类型是默认能够被序列化和反序列化的，那么如果一个对象的所有成员都是基础类型，则它是可以被序列化的。

(3) circe没有为Any类型和枚举类型实现Encoder和Decoder，也就是说circe无法自动序列化或反序列化Any和Enumeration；

(4) circe也没有为函数实现Encoder和Decoder，如果要实现函数的序列化和反序列化就需要了解scala是怎样将函数提拔为一等公民的；

(5) 泛型参数对encode和decode没有影响，我以为泛型擦除可能会导致丢失一些信息而无法序列化，测试证明我想多了。

---
预先定义的基础类：
```
package object circe {

  object HairAmount extends Enumeration {

    type HairAmount = Value

    val rich, soso, poor, empty = Value

    implicit val encoder: Encoder[Value] = Encoder.enumEncoder(HairAmount) // circe不支持枚举类型的自动序列化，HairAmount实现了Encoder使其可以被序列化
  }

  case class Person(name: String, age: Int) // Person的字段都是基础类型，且父类Object是可序列化的，因此它是可以自动序列化的。

  case class CodingMonkey(languageUse: String, person: Person) // Person可以自动序列化，所以它也是可以自动序列化的。

  case class CodingMonkeyWithHair(languageUse: String, person: Person, hairAmount: HairAmount) // HairAmount实现了Encoder可以序列化，那么它也是可以序列化的。

  case class CodingMonkeyWithSkilledLanguage[T](skill: T) // 这个稍微复杂点
  
  trait Language

  case class Scala(name: String = "Scala") exntends Language // 这里有坑，mark一下 (Trap 1)

  case class Haskell(name: String = "Haskell", whatever: Any = "whatever") extends Language // 注意这个Any，mark一下
}

```
### object => Json , 对象的Json序列化

在实现对象序列化时，建议先无脑import下面的包，反正无用的话IDE会告诉你，再删除也不迟，但是要是少了它们，出了问题可能就要浪费很多时间了。
```
import io.circe.{Encoder, Json}
import io.circe.syntax._
import io.circe.generic.auto._ // 这里有坑，mark一下
```

- **枚举类型的序列化**
```
implicit val encoder: Encoder[Value] = Encoder.enumEncoder(HairAmount)
```
因为枚举类型不能够自动序列化，所以需要自己实现Encoder，但是circe给了处理枚举类型的接口。

- **普通对象的序列化**

一个简陋的实例：
```
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

val person = Person("simplesoul", 18)

val monkey = CodingMonkey("scala", person)

val monkeyWithHair = CodingMonkeyWithHair("scala", person, HairAmount.rich)

encode(monkey).map(json => assert(json.noSpaces == """{"languageUse":"scala","person":{"name":"simplesoul","age":18}}"""))

encode(monkeyWithHair).map(json => assert(json.noSpaces == """{"languageUse":"scala","person":{"name":"simplesoul","age":18},"hairAmount":"rich"}"""))
```

注释中已经解释的比较清楚了，保证要序列化对象的所有字段都是可序列化的就OK了。

另外，因为泛型擦除对序列化没有影响，那么如果Person可以序列化，则List[Person]也是可以序列化的。

- **带泛型参数的对象的序列化** 

编译期间不会有任何问题，但是如果在运行期间，泛型参数具体到某个类型，如果这个类无法序列化，那么将报错。

**所以对含有泛型参数的对象进行序列化是有风险的，你无法预测泛型参数会在运行时指向哪个具体的类型，更无法知道这个类型是否能够序列化**

- **Any对象的序列化**

如果有一种方式能encode Any，那么就不会有这篇文档了(大家用这种方式encode一切，简单直接，circe也只需要俩函数了encodeAny和decodeAny)。目前没遇到一定要处理Any序列化的场景。

但是，虽然encode Any很难实现，但是可以使用泛型绕过Any，一定程度上适合部分场景。

重新考虑Haskell的定义，
```
case class Haskell(name: String = "Haskell", whatever: Any = "whatever")

case class Haskell[T](name: String, whatever: T = "whatever")
```
这样你就可以在实例化Hashkell时给whatever传入任何对象，只要它是可以序列化的，haskell的实例就是能够序列化的。

甚至，你可以下Haskell传入下面的Whatever，一点毛病都没有。
```
case class Whatever[V](foo: String, something: V)

val monkeyWithHaskell = CodingMonkeyWithSkilledLanguage(new Haskell("Hashkell", Whatever("hello world", person)))

encode(monkeyWithHaskell).map(json => 
    assert(json.noSpaces == """{"skill":{"name":"Hashkell","whatever":{"foo":"hello world","something":{"name":"simplesoul","age":18}}}"""))
```

---
So far so good, 但是还有遇到的几个坑要说明一下。

- 多态是个好东西，但是在circe里面需要注意一下，现在将CodingMonkey的实现多态化：
```
trait codeable // something can code

case class AI extends Codeable // 会敲代码的AI

case class Person(name: String, age: Int) extends Codeable

case class CodingMonkey(languageUse: String, codeable: Codeable)

val person = Person("simplesoul", 18) 
  
val monkey = CodingMonkey("scala", person) 
  
encode(monkey).map(json => assert(json.noSpaces == """{"languageUse":"scala","person":{"name":"simplesoul","age":18}}""")) // compile error
```
坑爹！编译通不过了，person和monkey的定义与前文一模一样，只是利用多态将CodingMonkey扩大到了任何会敲代码的对象，但是这个会敲代码的对象是一个trait，虽然在运行时传入的是trait的实例化子类person，但是方法签名就是一个trait，circe无法处理trait，也不会自动将codeable转型为Person。如果将Codeable声明为class，就能够通过编译了，而且符合期望实现的逻辑，只是不太符合设计原则，毕竟设计上将其声明为trait更合适。

那么，如果Codeable必须是trait怎么办？

circe提供了@JsonCodec注解解决这个问题，该注解需要macro的支持。首先要在build.sbt里面加入一个处理宏注解的插件：
```
addCompilerPlugin("org.scalamacros" %% "paradise" % "2.1.0" cross CrossVersion.full)
```
然后将codeable声明为:
```
@JsonCodec sealed trait Codeable // 必须是sealed trait
```
OK，everything back to right.

但是，如果Codeable的声明无法被修改，比如它在一个第三方的jar包中，咋整？

目前没遇到这种情况，只有一个思路：创建一个新trait继承Codeable，给他@JsonCodec注解。

- 能够序列化的对象却报 "Encoder not found".
```
import io.circe.generic.auto._
```
尝试import上面的代码。如果需要就序列化的对象是从项目内的其他文件import进来的，就必须import io.circe.generic.auto._。

**TODO 测试从第三方jar包引入的对象是否能通过io.circe.generic.auto._自动实现Encoder。(P1)** 

---
最后的问题：怎么自己实现一个Encoder？
```
val file = new File("xxx")

implicit val encodeFile: Encoder[File] = file => Map("name" -> file.getName).asJson
```
这只是一个简单的case，提供一个思路：把你需要序列化对象的所有字段信息存入Map中，然后asJson。