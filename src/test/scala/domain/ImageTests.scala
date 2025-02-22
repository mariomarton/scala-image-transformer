package domain

import org.scalatest.funsuite.AnyFunSuite

class ImageTests extends AnyFunSuite {

  test("Ensure Image points type can be some immutable subclass of Seq (like Vector)") {
    val vectorPoints: Vector[Vector[GrayscalePixel]] = Vector(
      Vector(GrayscalePixel(100.toShort), GrayscalePixel(200.toShort)),
      Vector(GrayscalePixel(150.toShort), GrayscalePixel(50.toShort))
    )

    val grayscaleImage = GrayscaleImage(vectorPoints)

    assert(grayscaleImage.getPoints == vectorPoints, "The points in the GrayscaleImage do not match the expected values.")
  }


  val asciiImagePoints: List[List[ASCIISymbol]] = List(
    List(
      ASCIISymbol('@'),
      ASCIISymbol('#')
    ),
    List(
      ASCIISymbol('*'),
      ASCIISymbol('.')
    )
  )

  val rgbImagePoints: List[List[RGBPixel]] = List(
    List(
      RGBPixel(255, 0, 0),
      RGBPixel(0, 255, 0)
    ),
    List(
      RGBPixel(0, 0, 255),
      RGBPixel(128, 128, 128)
    )
  )

  val grayscaleImagePoints: List[List[GrayscalePixel]] = List(
    List(
      GrayscalePixel(200.toShort),
      GrayscalePixel(100.toShort)
    ),
    List(
      GrayscalePixel(50.toShort),
      GrayscalePixel(0.toShort)
    )
  )

  val asciiImage: ASCIIImage = ASCIIImage(asciiImagePoints)

  val rgbImage: RGBImage = RGBImage(rgbImagePoints)

  val grayscaleImage: GrayscaleImage = GrayscaleImage(grayscaleImagePoints)

  // Generic test function for dimensions
  def testDimensions(image: RegularCustomImage[_], expectedDimensions: (Int, Int)): Unit = {
    assert(image.getDimensions == expectedDimensions)
  }

  test("Image dimensions") {
    testDimensions(asciiImage, (2, 2))
    testDimensions(rgbImage, (2, 2))
    testDimensions(grayscaleImage, (2, 2))
  }

  // Generic test function for point access
  def testPointAccess[P <: Point](image: RegularCustomImage[P], expectedPoints: List[List[P]]): Unit = {
    val actualPoints = image.getPoints
    for (x <- expectedPoints.indices; y <- expectedPoints(x).indices) {
      assert(actualPoints(x)(y) == expectedPoints(x)(y), s"Point at ($x, $y) did not match expected value.")
    }
  }

  test("Access ASCIIImage points") {
    testPointAccess(asciiImage, asciiImagePoints)
  }

  test("Access RGBImage points") {
    testPointAccess(rgbImage, rgbImagePoints)
  }

  test("Access GrayscaleImage points") {
    testPointAccess(grayscaleImage, grayscaleImagePoints)
  }

  // Generic test function for verifying RegularCustomImage type
  def testRegularCustomImage(image: RegularCustomImage[_]): Unit = {
    assert(image.isInstanceOf[RegularCustomImage[_]])
  }

  test("Verify RegularCustomImage instances") {
    testRegularCustomImage(asciiImage)
    testRegularCustomImage(rgbImage)
    testRegularCustomImage(grayscaleImage)
  }

  test("BufferedImageAdapter wraps and retrieves BufferedImage") {
    val bufferedImage = new java.awt.image.BufferedImage(100, 100, java.awt.image.BufferedImage.TYPE_INT_RGB)
    val adapter = new BufferedImageAdapter(bufferedImage)

    assert(adapter.getBufferedImage eq bufferedImage, "BufferedImageAdapter did not return the correct BufferedImage instance")
  }

  // Generic tests for getPoints
  def testGetPoints[P <: Point](image: RegularCustomImage[P], expectedPoints: Seq[Seq[P]]): Unit = {
    val className = image.getClass.getSimpleName  // Dynamically get the class name
    test(s"Test getPoints for $className") {
      val points = image.getPoints
      assert(points == expectedPoints, s"getPoints did not return the correct points for $className")
    }
  }

  testGetPoints(asciiImage, asciiImagePoints)
  testGetPoints(rgbImage, rgbImagePoints)
  testGetPoints(grayscaleImage, grayscaleImagePoints)
}
