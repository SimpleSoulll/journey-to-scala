package me.simplesoul.scalaL2.generic

object TypeErasure {

  def main(args: Array[String]): Unit = {

    val interviewee1 = new Interviewee(new Python())

    val interviewee2 = new Interviewee(new Haskell())

    assert(interviewee1.getClass == interviewee2.getClass) // true or false? why?
  }
}

class Interviewee[L](skilled: L) {

  def getSkilledLanguage: L = skilled
}

sealed class Language

class Python extends Language

class Java extends Language

class Haskell extends Language

class Cpp extends Language

