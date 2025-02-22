package domain

import collection.immutable.Seq

import constants.Constants.WHITE

/**
 * Base trait for a point that forms a part of an image.
 */
trait Point {}

/**
 * Trait for a point with a single value attribute.
 */
trait SimplePoint[T] extends Point {
  def value: T
}

/**
 * Trait for a point with a multiple color values.
 */
trait ColorPoint[T] extends Point {
  def colorValues: Seq[T]
}

case class ASCIISymbol(asciiValue: Char) extends SimplePoint[Char] {
  def value: Char = asciiValue
}

case class GrayscalePixel(grayscaleValue: Int) extends SimplePoint[Int] {
  def value: Int = grayscaleValue

  def invert: GrayscalePixel = {
    GrayscalePixel(WHITE - value)
  }
}

case class RGBPixel(redValue: Int, greenValue: Int, blueValue: Int) extends ColorPoint[Int] {
  def colorValues: Seq[Int] = Seq(redValue, greenValue, blueValue)
}
