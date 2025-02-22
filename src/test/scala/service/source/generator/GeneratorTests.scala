package service.source.generator

import constants.Constants as const
import domain.{RGBImage, RGBPixel}
import org.scalatest.funsuite.AnyFunSuite

class GeneratorTests extends AnyFunSuite {

  // Constant seed to ensure determinism
  val fixedSeed: Int = 12345

  // Helper function that creates a RandomImageGenerator and returns a generated image from it
  def CreateGeneratorAndGetImage(): RGBImage = {
    val generator = new RandomImageGenerator(Some(fixedSeed))
    generator.generateImage()
  }

  test("RandomImageGenerator generates valid RGBImage with random dimensions and pixel values") {
    val image = CreateGeneratorAndGetImage()

    assert(image.isInstanceOf[RGBImage], "RandomImageGenerator did not generate an instance of RGBImage.")
    assert(image.points.nonEmpty, "Generated image should have non-empty points.")

    val (width, height) = image.getDimensions
    assert(
      width >= const.MIN_RANDOM_IMAGE_WIDTH && width < const.MAX_RANDOM_IMAGE_WIDTH,
      s"Generated width $width is outside expected bounds."
    )
    assert(
      height >= const.MIN_RANDOM_IMAGE_HEIGHT && height < const.MAX_RANDOM_IMAGE_HEIGHT,
      s"Generated height $height is outside expected bounds."
    )

    val errorText = " pixel value of a generated picture is out of range."
    // Verify that all pixel values are within valid RGB range
    image.getPoints.flatten.foreach { pixel =>
      assert(pixel.redValue >= 0 && pixel.redValue <= 255, "Red" + errorText)
      assert(pixel.greenValue >= 0 && pixel.greenValue <= 255, "Green" + errorText)
      assert(pixel.blueValue >= 0 && pixel.blueValue <= 255, "Blue" + errorText)
    }
  }

  test("Two RandomImageGenerators produce the same images given the same seed.") {
    val image1 = CreateGeneratorAndGetImage()
    val image2 = CreateGeneratorAndGetImage()

    assert(image1 == image2, "Two RandomImageGenerators produced different images given the same seed.")
  }
}
