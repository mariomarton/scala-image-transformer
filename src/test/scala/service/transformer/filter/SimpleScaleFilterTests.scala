package service.transformer.filter

import org.scalatest.funsuite.AnyFunSuite
import domain.{GrayscaleImage, GrayscalePixel}
import testutils.TestUtils

import scala.collection.immutable.Seq

class SimpleScaleFilterTests extends AnyFunSuite {

  private def assertScale(inputData: Seq[Seq[Int]], scaleFactor: Double, expectedData: Seq[Seq[Int]]): Unit = {
    val inputImage = TestUtils.createGrayscaleImage(inputData)
    val expectedImage = TestUtils.createGrayscaleImage(expectedData)

    val filter = new SimpleScaleFilter(scaleFactor)
    val outputImage = filter.transform(inputImage)

    assert(outputImage.getPoints == expectedImage.getPoints)
  }

  // 0.25x Tests
  test("SimpleScaleFilter correctly scales down an image by 0.25") {
    assertScale(
      Seq(
        Seq(10, 20, 30, 40),
        Seq(50, 60, 70, 80),
        Seq(90, 100, 110, 120),
        Seq(130, 140, 150, 160)
      ),
      0.25,
      Seq(
        Seq(35, 55),
        Seq(115, 135)
      )
    )
  }

  test("SimpleScaleFilter correctly scales down a single-pixel image by 0.25") {
    assertScale(
      Seq(
        Seq(10)
      ),
      0.25,
      Seq(
        Seq(10)
      )
    )
  }

  test("SimpleScaleFilter correctly scales down an image with fewer than 2x2 pixels by 0.25") {
    assertScale(
      Seq(
        Seq(10, 20)
      ),
      0.25,
      Seq(
        Seq(15)
      )
    )
  }

  test("SimpleScaleFilter correctly scales down an image with odd number of rows by 0.25") {
    assertScale(
      Seq(
        Seq(10, 20),
        Seq(30, 40),
        Seq(50, 60)
      ),
      0.25,
      Seq(
        Seq(25),
        Seq(55)
      )
    )
  }

  test("SimpleScaleFilter correctly scales down an image with odd number of rows and columns by 0.25") {
    assertScale(
      Seq(
        Seq(10, 20, 30),
        Seq(40, 50, 60),
        Seq(70, 80, 90)
      ),
      0.25,
      Seq(
        Seq(30, 45),
        Seq(75, 90)
      )
    )
  }

  test("SimpleScaleFilter correctly handles an empty image when scaling down by 0.25") {
    assertScale(
      Seq.empty,
      0.25,
      Seq.empty
    )
  }

  // 1.00x Tests
  test("SimpleScaleFilter correctly handles an image with no scaling (factor = 1.0)") {
    assertScale(
      Seq(
        Seq(10, 20, 9, 9),
        Seq(30, 40, 8, 8),
        Seq(50, 60, 7, 7)
      ),
      1.0,
      Seq(
        Seq(10, 20, 9, 9),
        Seq(30, 40, 8, 8),
        Seq(50, 60, 7, 7)
      )
    )
  }

  test("SimpleScaleFilter correctly handles an empty image with no scaling (factor = 1.0)") {
    assertScale(
      Seq.empty,
      1.0,
      Seq.empty
    )
  }

  // 4.00x Tests
  test("SimpleScaleFilter correctly scales up an image by 4.0") {
    assertScale(
      Seq(
        Seq(10, 20),
        Seq(30, 40)
      ),
      4.0,
      Seq(
        Seq(10, 10, 20, 20),
        Seq(10, 10, 20, 20),
        Seq(30, 30, 40, 40),
        Seq(30, 30, 40, 40)
      )
    )
  }

  test("SimpleScaleFilter correctly scales up a single-pixel image by 4.0") {
    assertScale(
      Seq(
        Seq(10)
      ),
      4.0,
      Seq(
        Seq(10, 10),
        Seq(10, 10)
      )
    )
  }

  test("SimpleScaleFilter correctly scales up an image with one row only by 4.0") {
    assertScale(
      Seq(
        Seq(10, 20)
      ),
      4.0,
      Seq(
        Seq(10, 10, 20, 20),
        Seq(10, 10, 20, 20)
      )
    )
  }

  test("SimpleScaleFilter correctly scales up an image with one column only by 4.0") {
    assertScale(
      Seq(
        Seq(10),
        Seq(20)
      ),
      4.0,
      Seq(
        Seq(10, 10),
        Seq(10, 10),
        Seq(20, 20),
        Seq(20, 20)
      )
    )
  }

  test("SimpleScaleFilter correctly handles an empty image when scaling up by 4.0") {
    assertScale(
      Seq.empty,
      4.0,
      Seq.empty
    )
  }

}
