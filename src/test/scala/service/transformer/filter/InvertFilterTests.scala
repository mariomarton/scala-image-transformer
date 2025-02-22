package service.transformer.filter

import org.scalatest.funsuite.AnyFunSuite
import domain.{GrayscaleImage, GrayscalePixel}
import testutils.TestUtils

class InvertFilterTests extends AnyFunSuite {

  private def assertInversion(inputData: Seq[Seq[Int]], expectedData: Seq[Seq[Int]]): Unit = {
    val inputImage = TestUtils.createGrayscaleImage(inputData)
    val expectedImage = TestUtils.createGrayscaleImage(expectedData)

    val filter = new InvertFilter
    val outputImage = filter.transform(inputImage)

    assert(outputImage.getPoints == expectedImage.getPoints)
  }

  // Tests
  test("InvertFilter correctly inverts a simple grayscale image") {
    assertInversion(
      Seq(
        Seq(0, 128, 255),
        Seq(64, 192, 32)
      ),
      Seq(
        Seq(255, 127, 0),
        Seq(191, 63, 223)
      )
    )
  }

  test("InvertFilter correctly handles an image with all pixels at maximum value (255)") {
    assertInversion(
      Seq.fill(3, 3)(255),
      Seq.fill(3, 3)(0)
    )
  }

  test("InvertFilter correctly handles an image with all pixels at minimum value (0)") {
    assertInversion(
      Seq.fill(3, 3)(0),
      Seq.fill(3, 3)(255)
    )
  }

  test("InvertFilter correctly handles an empty image") {
    assertInversion(
      Seq.empty,
      Seq.empty
    )
  }

  test("InvertFilter correctly handles a single-pixel image") {
    assertInversion(
      Seq(Seq(128)),
      Seq(Seq(127))
    )
  }

  test("InvertFilter correctly inverts a larger image") {
    val size = 240
    val inputData = Seq.fill(size, size)(128)
    val expectedData = Seq.fill(size, size)(127)

    assertInversion(inputData, expectedData)
  }
}
