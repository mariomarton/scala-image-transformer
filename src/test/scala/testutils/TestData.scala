package testutils

import domain.{ASCIIImage, ASCIISymbol, RGBImage, RGBPixel}

object TestData {

  // Single pixel (4 variants of the same data)
  val SinglePixelImage: List[List[(Int, Int, Int)]] = List(
    List((4, 8, 15))
  )

  val SinglePixelImageRGB: RGBImage = RGBImage(List(List(RGBPixel(4, 8, 15))))

  val SinglePixelGrayscaleImage: List[List[Int]] = List(List(8))

  val SinglePixelGrayscaleImageASCII: ASCIIImage = ASCIIImage(List(List(ASCIISymbol('I'))))

  // Simple image
  val SimpleImage: List[List[(Int, Int, Int)]] = List(
    List((0, 0, 0), (16, 16, 16)),
    List((32, 32, 32), (48, 48, 48)),
    List((64, 65, 66), (67, 68, 69))
  )

  val SimpleImageRGB: RGBImage = RGBImage(List(
    List(RGBPixel(0, 0, 0),     RGBPixel(16, 16, 16)),
    List(RGBPixel(32, 32, 32),  RGBPixel(48, 48, 48)),
    List(RGBPixel(64, 65, 66),  RGBPixel(67, 68, 69))
  ))

  val SimpleGrayscaleImage: List[List[Int]] = List(
    List(0, 16),
    List(32, 48),
    List(65, 68)
  )

  val SimpleGrayscaleImageASCII: ASCIIImage = ASCIIImage(List(
    List(ASCIISymbol('A'), ASCIISymbol('Q')),
    List(ASCIISymbol('G'), ASCIISymbol('W')),
    List(ASCIISymbol('N'), ASCIISymbol('Q'))
  ))

  val SquareGrayscaleImage: Seq[Seq[Int]] = Seq(
    Seq(1, 2, 3),
    Seq(4, 5, 6),
    Seq(7, 8, 9)
  )

  // Uniform image (same values in each pixel)
  val UniformImage: List[List[(Int, Int, Int)]] = List.fill(8, 8)((0, 150, 255))

  val UniformImageRGB: RGBImage = RGBImage(List.fill(8, 8)(RGBPixel(0, 150, 255)))

  val UniformGrayscaleImage: List[List[Int]] = List.fill(8, 8)(117)

  val UniformGrayscaleImageASCII: ASCIIImage = ASCIIImage(List.fill(8, 8)(ASCIISymbol('N')))

  // Large image
  def LargeImage: Seq[Seq[(Int, Int, Int)]] =
    TestUtils.generateRandomPixels(1280, 720, seed = 12345)

  def LargeGrayscaleImage: Seq[Seq[Int]] =
    TestUtils.generateRandomGrayscalePixels(1280, 720, seed = 12345)
}
