package service.transformer.filter

import org.scalatest.funsuite.AnyFunSuite
import testutils.{TestData, TestUtils}
import domain.GrayscaleImage

class SimpleRotateFilterTests extends AnyFunSuite {

  private def assertRotation(input: Seq[Seq[Int]], degrees: Int, expected: Seq[Seq[Int]]): Unit = {
    val filter = new SimpleRotateFilter(degrees)
    val inputImage = TestUtils.createGrayscaleImage(input)
    val outputImage = filter.transform(inputImage)

    val expectedPoints = TestUtils.intsToGrayscalePixels(expected)
    assert(outputImage.getPoints == expectedPoints)
  }

  // Helper data (based on original data in TestData file)
  private val SimpleGrayscaleImage90: Seq[Seq[Int]] = Seq(
    Seq(65, 32, 0),
    Seq(68, 48, 16)
  )

  private val SimpleGrayscaleImage180: Seq[Seq[Int]] = Seq(
    List(68, 65),
    List(48, 32),
    List(16, 0)
  )

  private val SimpleGrayscaleImage270: Seq[Seq[Int]] = Seq(
    Seq(16, 48, 68),
    Seq(0 , 32, 65)
  )

  // Now to tests:
  // 90 degrees
  test("SimpleRotateFilter correctly rotates a non-square image: 90 degrees") {
    assertRotation(TestData.SimpleGrayscaleImage, 90, SimpleGrayscaleImage90)
  }

  test("SimpleRotateFilter correctly rotates an image: 450 degrees (=90 degrees)") {
    assertRotation(TestData.SimpleGrayscaleImage, 450, SimpleGrayscaleImage90)
  }

  test("SimpleRotateFilter correctly rotates a square image: 90 degrees") {
    assertRotation(TestData.SquareGrayscaleImage, 90, Seq(
      Seq(7, 4, 1),
      Seq(8, 5, 2),
      Seq(9, 6, 3)
    ))
  }

  // 180 degrees
  test("SimpleRotateFilter correctly rotates a square image: 180 degrees") {
    assertRotation(TestData.SquareGrayscaleImage, 180, Seq(
      Seq(9, 8, 7),
      Seq(6, 5, 4),
      Seq(3, 2, 1)
    ))
  }

  test("SimpleRotateFilter correctly rotates a non-square image: 180 degrees") {
    assertRotation(TestData.SimpleGrayscaleImage, 180, SimpleGrayscaleImage180)
  }

  // -90 / 270 degrees
  test("SimpleRotateFilter correctly rotates a non-square image: -90 degrees") {
    assertRotation(TestData.SimpleGrayscaleImage, -90, SimpleGrayscaleImage270)
  }

  test("SimpleRotateFilter correctly rotates a square image: -90 degrees") {
    assertRotation(TestData.SquareGrayscaleImage, -90, Seq(
      Seq(3, 6, 9),
      Seq(2, 5, 8),
      Seq(1, 4, 7)
    ))
  }

  test("SimpleRotateFilter correctly rotates a square image: 270 degrees") {
    assertRotation(TestData.SquareGrayscaleImage, 270, Seq(
      Seq(3, 6, 9),
      Seq(2, 5, 8),
      Seq(1, 4, 7)
    ))
  }

  // 360 degrees
  test("SimpleRotateFilter correctly rotates non-square image: 360 degrees") {
    assertRotation(TestData.SimpleGrayscaleImage, 360, TestData.SimpleGrayscaleImage)
  }

  // Additional tests
  test("SimpleRotateFilter: One -90 degree rotation equals three 90 degree rotations") {
    val inputImage = TestUtils.createGrayscaleImage(TestData.SimpleGrayscaleImage)

    val filterPos90 = new SimpleRotateFilter(90)
    val filterNeg90 = new SimpleRotateFilter(-90)

    val outputPos90x3 = filterPos90.transform(filterPos90.transform(filterPos90.transform(inputImage)))
    val outputNeg90x1 = filterNeg90.transform(inputImage)

    assert(outputPos90x3.getPoints == outputNeg90x1.getPoints)
  }

  test("SimpleRotateFilter correctly rotates a very wide image.") {
    assertRotation(Seq.fill(1, 20)(1), 90, Seq.fill(20, 1)(1))
  }

  test("SimpleRotateFilter correctly rotates a very tall image.") {
    assertRotation(Seq.fill(20, 1)(1), 90, Seq.fill(1, 20)(1))
  }

  test("SimpleRotateFilter correctly rotates a very large image.") {
    val largeImage = TestData.LargeGrayscaleImage.map(_.toSeq)
    val reversedImage = largeImage.reverse.map(_.reverse)
    assertRotation(largeImage, 180, reversedImage)
  }

  test("SimpleRotateFilter correctly handles a single-pixel image.") {
    assertRotation(TestData.SinglePixelGrayscaleImage, 90, TestData.SinglePixelGrayscaleImage)
  }

  test("SimpleRotateFilter correctly handles an empty image.") {
    assertRotation(Seq.empty, 90, Seq.empty)
  }
}
