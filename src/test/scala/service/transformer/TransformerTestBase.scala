package service.transformer

import org.scalatest.funsuite.AnyFunSuite
import domain.ImageBase

/**
 * Base class for testing transformers.
 * @tparam I Input image type
 * @tparam O Output image type
 * @tparam P Pixel type for testing (e.g., (Int, Int, Int) for RGB or Int for grayscale)
 */
abstract class TransformerTestBase[I <: ImageBase, O <: ImageBase, P] extends AnyFunSuite {

  val transformer: Transformer[O] { type Input = I }

  /**
   * Abstract method for creating input images from pixel values.
   */
  def createInputImage(pixelValues: Seq[Seq[P]]): I

  /**
   * Abstract method for validating that result is the correctly transformed inputImage
   * @param inputPixelValues The input pixel values.
   * @param result The transformed output image.
   */
  def validateOutput(inputPixelValues: Seq[Seq[P]], result: O): Unit

  /**
   * Helper method for validating a single transformation.
   * The expected output is calculated dynamically here, by the validateOutput method.
   * @param pixelValues The input pixel values.
   * @param description A description of what the test verifies.
   */
  protected def validateTransformation(
                                        pixelValues: Seq[Seq[P]],
                                        description: String
                                      ): Unit = {
    test(prependClassName(description)) {
      val inputImage = createInputImage(pixelValues)
      val result = transformer.transform(inputImage)
      validateOutput(pixelValues, result)
    }
  }

  /**
   * Helper method for validating a single transformation against a fixed expected result.
   *
   * @param pixelValues    The input pixel values.
   * @param expectedOutput The expected output.
   * @param description    A description of what the test verifies.
   * @param assertMessage  Message printed when fixed result doesn't equal the transformed input.
   */
  protected def validateFixedResultTransformation(
                                                   pixelValues: Seq[Seq[P]],
                                                   expectedOutput: O,
                                                   description: String,
                                                   assertMessage: String
                                                 ): Unit = {
    test(prependClassName(description)) {
      val inputImage = createInputImage(pixelValues)
      val result = transformer.transform(inputImage)
      assert(
        result == expectedOutput,
        prependClassName(assertMessage)
      )
    }
  }

  /**
   * Prepends the transformer's class name to a string
   */
  private def prependClassName(text: String): String = {
    s"${transformer.getClass.getSimpleName} $text"
  }

  def runDefaultTests(): Unit = {
    validateFixedResultTransformation(
      singlePixelData,
      singlePixelDataTransformed,
      description = "correctly transforms a single-pixel image",
      assertMessage = "did not transforms a single-pixel image correctly"
    )

    validateFixedResultTransformation(
      simpleImageData,
      simpleImageDataTransformed,
      description = "correctly transforms a simple image",
      assertMessage = "did not transforms a simple image correctly"
    )

    validateFixedResultTransformation(
      uniformImageData,
      uniformImageDataTransformed,
      description = "handles uniform pixel values correctly",
      assertMessage = "did not transforms a uniform image correctly (image with identical pixels)"
    )

    validateTransformation(
      largeImageData,
      description = "correctly transforms a large image"
    )
  }

  // Abstract pixel data and their respective expected outputs
  def singlePixelData: Seq[Seq[P]]
  def simpleImageData: Seq[Seq[P]]
  def uniformImageData: Seq[Seq[P]]
  def largeImageData: Seq[Seq[P]] // No expected output for this one, it gets checked dynamically.

  def singlePixelDataTransformed: O
  def simpleImageDataTransformed: O
  def uniformImageDataTransformed: O
}

/**
 * The same as TransformerTestBase, but with an added test for transforming an empty image.
 */
abstract class TransformerTestBaseWithEmptyImage[I <: ImageBase, O <: ImageBase, P] extends TransformerTestBase[I, O, P] {
  def createEmptyOutputImage(): O

  def runEmptyImageInputTest(): Unit = {
    validateFixedResultTransformation(
      Seq(Seq.empty),
      createEmptyOutputImage(),
      description = "correctly handles an empty image",
      assertMessage = "did not transforms an empty image correctly"
    )
  }
}
