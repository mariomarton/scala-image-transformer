package service.transformer.grayscaletoascii

import domain.{ASCIIImage, ASCIISymbol, GrayscaleImage, GrayscalePixel}
import service.transformer.{Transformer, TransformerTestBase, TransformerTestBaseWithEmptyImage}
import testutils.TestData.{LargeGrayscaleImage, SimpleGrayscaleImage, SimpleGrayscaleImageASCII, SinglePixelGrayscaleImage, SinglePixelGrayscaleImageASCII, UniformGrayscaleImage, UniformGrayscaleImageASCII}

/**
 * Tests for GrayscaleToASCIIImageTransformer using TransformerTestBase.
 */
class GrayscaleToASCIIImageTransformerTests
  extends TransformerTestBaseWithEmptyImage[GrayscaleImage, ASCIIImage, Int] {

  private val testAlgorithm = new TestGrayscaleToASCIIAlgorithm()
  override val transformer: Transformer[ASCIIImage] {type Input = GrayscaleImage} =
    new GrayscaleToASCIIImageTransformer(testAlgorithm)

  override def createInputImage(pixelValues: Seq[Seq[Int]]): GrayscaleImage = {
    val grayscalePoints = pixelValues.map(_.map(GrayscalePixel.apply))
    GrayscaleImage(grayscalePoints)
  }

  override def validateOutput(inputPixelValues: Seq[Seq[Int]], result: ASCIIImage): Unit = {
    inputPixelValues.zip(result.points).foreach { case (inputRow, outputRow) =>
      inputRow.zip(outputRow).foreach { case (grayscaleValue, asciiSymbol) =>
        val expectedSymbol = ASCIISymbol((grayscaleValue % 26 + 'A').toChar)
        assert(
          asciiSymbol == expectedSymbol,
          s"ASCII symbol mismatch: expected '${expectedSymbol.value}', got '${asciiSymbol.value}' for grayscale value $grayscaleValue"
        )
      }
    }
  }

  override def createEmptyOutputImage(): ASCIIImage = ASCIIImage(Seq(Seq.empty))

  override def singlePixelData: Seq[Seq[Int]] = SinglePixelGrayscaleImage
  override def singlePixelDataTransformed: ASCIIImage = SinglePixelGrayscaleImageASCII

  override def simpleImageData: Seq[Seq[Int]] = SimpleGrayscaleImage
  override def simpleImageDataTransformed: ASCIIImage = SimpleGrayscaleImageASCII

  override def uniformImageData: Seq[Seq[Int]] = UniformGrayscaleImage
  override def uniformImageDataTransformed: ASCIIImage = UniformGrayscaleImageASCII

  override def largeImageData: Seq[Seq[Int]] = LargeGrayscaleImage

  runDefaultTests()
  runEmptyImageInputTest()

  validateFixedResultTransformation(
    Seq(Seq(0, 255)),
    ASCIIImage(Seq(Seq(ASCIISymbol('A'), ASCIISymbol('V')))),
    description = "correctly handles boundary grayscale values (0 and 255)",
    assertMessage = "does not correctly handle boundary grayscale values (0 and 255)"
  )
}

class TestGrayscaleToASCIIAlgorithm extends GrayscaleToASCIIAlgorithm {
  override def run(image: GrayscaleImage): ASCIIImage = {
    // Simple mapping for testing (only A-Z values)
    ASCIIImage(image.getPoints.map(_.map(pixel => ASCIISymbol((pixel.value % 26 + 'A').toChar))))
  }
}

