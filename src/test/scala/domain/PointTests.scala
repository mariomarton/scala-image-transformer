package domain

import org.scalatest.funsuite.AnyFunSuite

import constants.Constants.WHITE

class PointTests extends AnyFunSuite {

  test("ASCIISymbol value") {
    val symbol = ASCIISymbol('@')
    assert(symbol.value == '@')
  }

  test("GrayscalePixel value") {
    val grayscalePixel = GrayscalePixel(255)
    assert(grayscalePixel.value == 255)
  }

  test("RGBPixel color values") {
    val rgbPixel = RGBPixel(255, 0, 128)
    assert(rgbPixel.colorValues == Seq(255, 0, 128))
  }

  test("RGBPixel red, green, blue values individually") {
    val rgbPixel = RGBPixel(4, 8, 155)
    assert(rgbPixel.redValue == 4)
    assert(rgbPixel.greenValue == 8)
    assert(rgbPixel.blueValue == 155)
  }

  // Test polymorphism
  test("Polymorphic behavior of SimplePoint") {
    val point: SimplePoint[Int] = GrayscalePixel(200)
    assert(point.value == 200)
  }

  test("Polymorphic behavior of ColorPoint") {
    val point: ColorPoint[Int] = RGBPixel(10, 20, 30)
    assert(point.colorValues == Seq(10, 20, 30))
  }

  // Equality and construction test
  test("Equality of ASCIISymbols") {
    val symbol1 = ASCIISymbol('A')
    val symbol2 = ASCIISymbol('A')
    val symbol3 = ASCIISymbol('B')
    assert(symbol1 == symbol2)
    assert(symbol1 != symbol3)
  }

  test("Equality of GrayscalePixels") {
    val pixel1 = GrayscalePixel(50)
    val pixel2 = GrayscalePixel(50)
    val pixel3 = GrayscalePixel(100)
    assert(pixel1 == pixel2)
    assert(pixel1 != pixel3)
  }

  test("Equality of RGBPixels") {
    val pixel1 = RGBPixel(0, 0, 0)
    val pixel2 = RGBPixel(0, 0, 0)
    val pixel3 = RGBPixel(255, 255, 255)
    assert(pixel1 == pixel2)
    assert(pixel1 != pixel3)
  }

  test("GrayscalePixel inversion produces correct result") {
    val originalPixel = GrayscalePixel(100)
    val invertedPixel = originalPixel.invert
    val expectedInvertedPixel = GrayscalePixel(WHITE - 100)

    assert(invertedPixel == expectedInvertedPixel, "The inverted pixel does not match the expected value.")
  }

  test("GrayscalePixel inversion does not modify the original pixel") {
    val originalPixel = GrayscalePixel(100)
    originalPixel.invert

    assert(originalPixel.value == 100, "The original pixel should remain unchanged after inversion.")
  }
}
