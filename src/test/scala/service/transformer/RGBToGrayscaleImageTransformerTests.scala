package service.transformer

import domain.{GrayscaleImage, RGBImage, RGBPixel}
import testutils.TestUtils.{calculateGrayscale, intsToGrayscalePixels}
import testutils.TestData.{LargeImage, SimpleGrayscaleImage, SimpleImage, SinglePixelGrayscaleImage, SinglePixelImage, UniformGrayscaleImage, UniformImage}

/**
 * Tests for RGBToGrayscaleImageTransformer using TransformerTestBase.
 */
class RGBToGrayscaleImageTransformerTests
  extends TransformerTestBaseWithEmptyImage[RGBImage, GrayscaleImage, (Int, Int, Int)] {

  override val transformer: Transformer[GrayscaleImage] { type Input = RGBImage } =
    new RGBToGrayscaleImageTransformer()

  override def createInputImage(pixelValues: Seq[Seq[(Int, Int, Int)]]): RGBImage = {
    RGBImage(
      pixelValues.map(_.map { case (r, g, b) => RGBPixel(r, g, b) })
    )
  }

  override def validateOutput(inputPixelValues: Seq[Seq[(Int, Int, Int)]], result: GrayscaleImage): Unit = {
    val grayscaleFunction = (r: Int, g: Int, b: Int) => calculateGrayscale(r, g, b)

    result.points.zip(inputPixelValues).foreach { case (grayscaleRow, originalRow) =>
      grayscaleRow.zip(originalRow).foreach { case (grayscalePixel, (r, g, b)) =>
        assert(
          grayscalePixel.value == grayscaleFunction(r, g, b),
          s"Grayscale value mismatch for pixel ($r, $g, $b)"
        )
      }
    }
  }

  override def createEmptyOutputImage(): GrayscaleImage = GrayscaleImage(List(List.empty))

  override def singlePixelData: Seq[Seq[(Int, Int, Int)]] = SinglePixelImage
  override def singlePixelDataTransformed: GrayscaleImage = GrayscaleImage(intsToGrayscalePixels(SinglePixelGrayscaleImage))
  
  override def simpleImageData: Seq[Seq[(Int, Int, Int)]] = SimpleImage
  override def simpleImageDataTransformed: GrayscaleImage = GrayscaleImage(intsToGrayscalePixels(SimpleGrayscaleImage))
  
  override def uniformImageData: Seq[Seq[(Int, Int, Int)]] = UniformImage
  override def uniformImageDataTransformed: GrayscaleImage = GrayscaleImage(intsToGrayscalePixels(UniformGrayscaleImage))
  
  override def largeImageData: Seq[Seq[(Int, Int, Int)]] = LargeImage

  runDefaultTests()
  runEmptyImageInputTest()
}
