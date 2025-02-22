package service.transformer.grayscaletoascii

import domain.{ASCIIImage, GrayscaleImage}
import service.transformer.RegularToRegularImageTransformer

/**
 * Transformer to convert a GrayscaleImage into an ASCIIImage.
 * Delegates the grayscale-to-ASCII conversion logic to an algorithm.
 */
class GrayscaleToASCIIImageTransformer(
                                        private var algorithm: GrayscaleToASCIIAlgorithm
                                      ) extends RegularToRegularImageTransformer[ASCIIImage] {

  type Input = GrayscaleImage

  /**
   * Transforms a GrayscaleImage into an ASCIIImage using the provided algorithm.
   *
   * @param image the GrayscaleImage to transform
   * @return the resulting ASCIIImage
   */
  override def transform(image: GrayscaleImage): ASCIIImage = algorithm.run(image)

  /**
   * Updates the current algorithm for the grayscale-to-ASCII transformation.
   *
   * @param newAlgorithm the new algorithm to use
   */
  def updateAlgorithm(newAlgorithm: GrayscaleToASCIIAlgorithm): Unit = {
    this.algorithm = newAlgorithm
  }
}
