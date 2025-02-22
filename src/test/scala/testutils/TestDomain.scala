package testutils

import domain.{ColorPoint, SimplePoint, CustomImage, RegularCustomImage}

// Case classes of Point for testing only:

case class TestASCIISymbol(asciiValue: Char) extends SimplePoint[Char] {
  def value: Char = asciiValue
}

case class TestGrayscalePixel(grayscaleValue: Int) extends SimplePoint[Int] {
  def value: Int = grayscaleValue
}

case class TestRGBPixel(redValue: Int, greenValue: Int, blueValue: Int) extends ColorPoint[Int] {
  def colorValues: Seq[Int] = Seq(redValue, greenValue, blueValue)
}

//case class TestImage[T](points: List[List[T]]) extends CustomImage[T] {}

case class TestRGBImage(points: List[List[TestRGBPixel]]) extends RegularCustomImage[TestRGBPixel] {}
