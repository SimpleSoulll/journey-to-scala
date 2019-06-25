package me.simplesoul.util

import scala.reflect.runtime.universe

package object Validator {
  val mirror = universe.runtimeMirror(getClass.getClassLoader)
}
