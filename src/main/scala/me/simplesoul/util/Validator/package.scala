package me.simplesoul.util

import me.simplesoul.util.Validator.Validator.getClass

import scala.reflect.runtime.universe

package object Validator {
  val mirror = universe.runtimeMirror(getClass.getClassLoader)
}
