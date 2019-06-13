package me.simplesoul.topics.dsl.internal

import me.simplesoul.util.Validator.Person

// get[X] on "xxx" returning[Y] by func describe as desc

case class Router(expr: Expr *)

trait Expr {

  type T

  val content: T
}

case class Path(path: String)
case class ReqEntity[T]()
case class ResEntity[T]()
case class Description(router: Router)

trait Handler

case class Handler0[A](handler: A => ()) extends Handler

class HttpRouterImpl {

  implicit class Receiver (router: Router) {
    def on(path: String): Router = {
      Router(router.expr :+ new Expr{
        override type T = Path
        override val content: T = Path(path)
      }: _*)
    }

    def returning[Y](handler: handle.type) = {
      Router(router.expr :+ new Expr{
        override type T = ResEntity[Y]
        override val content: T = ???
      }: _*)
    }

    def by[A](handler: A => ()): Router = {
      Router(router.expr :+ new Expr{
        override type T = Handler0[A]
        override val content: T = Handler0(handler)
      }: _*)
    }

    def description(desc: String): Unit = {

    }
  }





  def test = {


    val f: String => () = {_ => ()}

    Get[Person] on "" returning[Person] handle by f description ""
  }

}

case object handle



object Get {

  def apply[T](): Router = {
    Router(new Expr {
      override type T = ReqEntity[T]
      override val content: T = ???
    })
  }
}

