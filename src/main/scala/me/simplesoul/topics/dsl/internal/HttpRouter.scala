package me.simplesoul.topics.dsl.internal

import akka.http.scaladsl.server
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import scala.reflect.runtime.universe._

object HttpRouter extends App {

  import HttpRouterImpl._

  val service = new Service

  Post[A] on "hello/world" returning[B] handle by service.toUpperCase _ describeAs "transform lower case to upper case" // 下划线将service.toUpperCase方法转换成函数
}

class Service {

  def toUpperCase(a: A): String = a.lowerCase.toUpperCase
}

case class A(lowerCase: String)
case class B(upperCase: String)

case class Router[X, Y](expr: Expr)

trait Expr {

  type T

  def get: T
}

trait Handler

case class Handler0[A](handler: A => Unit) extends Handler
case class Handler1[A, B](handler: A => B) extends Handler


object HttpRouterImpl {

  implicit class Parser[X: TypeTag, Y: TypeTag] (router: Router[X, Y]) extends FailFastCirceSupport {

    def on(path: String): Router[X, Y] = {
      Router(new Expr {
        override type T = Directive0
        override def get: T = akka.http.scaladsl.server.Directives.path(path)
      })
    }

    def returning[T](handler: handle.type): Router[X, T] = {
      Router[X, T](router.expr)
    }

    def by[A, B](handler: A => B)(implicit decoder: Decoder[X], encoder: Encoder[Y]): Router[X, Y] = {
      val route = router.expr.get.asInstanceOf[Directive0] {
        entity(as[X]) { x =>
          val resp = handler(x.asInstanceOf[A]).asInstanceOf[Y]
          complete(resp)
        }
      }
      Router(new Expr {
        override type T = Route
        override def get: T = route
      })
    }

    def describeAs(desc: String): Router[X, Y] = {
      // 处理swager
      router
    }
  }

  implicit class Combiner[X, Y](router: Router[X, Y]) {
    def +++[X1, Y1](router1: Router[X1, Y1]): server.Route = {
      router1.expr.asInstanceOf[Route] ~ router1.expr.asInstanceOf[Route]
    }
  }
}

case object handle

object HttpMethodEnumeration extends Enumeration {

  type HttpMethodEnumeration = Value

  val GET, POST, PUT, DELETE = Value
}

object Post {

  def apply[X](): Router[X, _] = {
    Router(new Expr {
      override type T = Directive0
      override def get: T = post
    })
  }
}
