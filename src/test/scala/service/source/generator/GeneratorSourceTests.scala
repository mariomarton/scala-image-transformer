package service.source.generator

import constants.Constants as const
import domain.{RGBImage, RGBPixel}
import org.scalatest.funsuite.AnyFunSuite

class GeneratorSourceTests extends AnyFunSuite {

  test("GeneratorSource retrieves an image from the provided Generator") {
    // Define a simple test generator

    val testPixels = List(
      List(RGBPixel(4, 8, 15), RGBPixel(16, 23, 42)),
      List(RGBPixel(128, 12, 255), RGBPixel(13, 14, 15))
    )
    class TestGenerator extends Generator[RGBImage] {
      override def generateImage(): RGBImage = {
        RGBImage(testPixels)
      }
    }

    val generator = new TestGenerator
    val generatorSource = new GeneratorSource(generator)

    val image = generatorSource.getImage

    assert(image.isInstanceOf[RGBImage], "GeneratorSource did not return an instance of RGBImage.")
    assert(image.points.nonEmpty, "GeneratorSource returned an empty image.")
    assert(image.getDimensions == (2, 2), "Generated image dimensions are incorrect.")
    assert(image.getPoints == testPixels, "Generated image's pixels are not matching the expected values")
  }
}
