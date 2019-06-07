circe是scala处理json比较成熟的工具之一，scala处理json常用的工具还有json4s。

circe依赖([maven]):
"io.circe" %% "circe-core" % "0.10.0",
"io.circe" %% "circe-generic" % "0.10.0",
"io.circe" %% "circe-parser" % "0.10.0",
"io.circe" %% "circe-optics" % "0.10.0",

json处理的实质就是序列化和反序列化，将特定对象序列化成Json对象的操作可以称为json序列化，将Json对象反序列化成特定对象的操作可以被成为Json反序列化，在circe中这两个操作分别称为Decode和Encode。更一般地，有时需要将特定对象序列化成Json对象，然后序列化成字符串，或将字符串反序列化成Json对象，然后将Json对象反序列化成特定对象。

Json对象和字符串之间的序列化和反序列化比较简单：
Json <=> String
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
                                      |}""".stripMargin)

在说明decode和encode之前先明确以下几点,以下所说的序列化都是指Json序列化：

(1) decode和encode的实质是以Json形式进行对象序列化或反序列化，因此Java对象序列化的概念和注解(如@transient)适用于decode和encode过程；在对象序列化中"**一个对象能够被序列化的前提是它的所有成员都能够被序列化**"的概念适用于Json序列化；

(2) decode过程依赖隐式参数Decoder，encode的过程依赖隐式参数Encoder，circe为所有基类自动实现了Encoder和Decoder，也就是说基础类型是默认能够被序列化和反序列化的，那么如果一个对象的所有成员都是基础类型，则它是可以被序列化的。

(3) circe没有为Any类型和枚举类型实现Encoder和Decoder，也就是说circe无法自动序列化或反序列化Any和Enumeration；

(4) circe也没有为函数实现Encoder和Decoder，如果要实现函数的序列化和反序列化就需要了解scala是怎样将函数提拔为一等公民的，参见函数相关内容；


object => Json
