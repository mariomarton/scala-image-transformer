package service.transformer

import domain.{BufferedImageAdapter, RGBImage, RGBPixel}
import testutils.TestUtils.{createBufferedImage, tuplesToRGBPixels}
import testutils.TestData.{LargeImage, SimpleImage, SimpleImageRGB, SinglePixelImage, SinglePixelImageRGB, UniformImage, UniformImageRGB}

/**
 * Tests for BufferedToRGBImageTransformer using TransformerTestBase.
 */
class BufferedToRGBImageTransformerTests
  extends TransformerTestBase[BufferedImageAdapter, RGBImage, (Int, Int, Int)] {

  override val transformer: Transformer[RGBImage] { type Input = BufferedImageAdapter } =
    new BufferedToRGBImageTransformer()
  
  override def createInputImage(pixelValues: Seq[Seq[(Int, Int, Int)]]): BufferedImageAdapter = {
    val bufferedImage = createBufferedImage(pixelValues)
    new BufferedImageAdapter(bufferedImage)
  }
  
  override def validateOutput(inputPixelValues: Seq[Seq[(Int, Int, Int)]], result: RGBImage): Unit = {
    val expectedPixels = tuplesToRGBPixels(inputPixelValues)
    assert(result.points == expectedPixels, s"Transformed image points should match the input pixels. " +
      s"Expected: ${expectedPixels}, Got: ${result.points}")
  }
  
  override def singlePixelData: Seq[Seq[(Int, Int, Int)]] = SinglePixelImage
  override def singlePixelDataTransformed: RGBImage = SinglePixelImageRGB
  
  override def simpleImageData: Seq[Seq[(Int, Int, Int)]] = SimpleImage
  override def simpleImageDataTransformed: RGBImage = SimpleImageRGB
  
  override def uniformImageData: Seq[Seq[(Int, Int, Int)]] = UniformImage
  override def uniformImageDataTransformed: RGBImage = UniformImageRGB
  
  override def largeImageData: Seq[Seq[(Int, Int, Int)]] = LargeImage

  runDefaultTests()
  
  // No tests for an empty image or image with a dimension of size 0,
  // because BufferedImage constructor doesn't allow them
}
