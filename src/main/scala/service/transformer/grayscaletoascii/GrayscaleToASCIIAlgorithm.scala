package service.transformer.grayscaletoascii

import domain.{ASCIIImage, GrayscaleImage}

/**
 * Trait representing an algorithm for converting grayscale values to ASCII characters.
 *
 * Allows the implementation of different strategies (e.g., linear tables, non-linear mappings, custom logic).
 */
trait GrayscaleToASCIIAlgorithm {

  /**
   * Runs the algorithm, transforming the GrayscaleImage to an ASCIIImage.
   *
   * @param image the GrayscaleImage to transform
   * @return the resulting ASCIIImage
   */
  def run(image: GrayscaleImage): ASCIIImage
}

/**
 * Concrete implementation of a table-based grayscale-to-ASCII conversion algorithm.
 *
 * Transforms a GrayscaleImage to an ASCIIImage using a provided grayscale-to-ASCII table.
 *
 * @param table the table used for mapping grayscale values to ASCII characters
 */
class TableBasedGrayscaleToASCIIAlgorithm(val table: GrayscaleToASCIITable) extends GrayscaleToASCIIAlgorithm {

  /**
   * Runs the algorithm, transforming the GrayscaleImage to an ASCIIImage using table-based conversion.
   *
   * @param image the GrayscaleImage to transform
   * @return the resulting ASCIIImage
   */
  override def run(image: GrayscaleImage): ASCIIImage = {
    val asciiPixels = image.getPoints.map(_.map { grayscalePixel =>
      table.getASCIISymbol(grayscalePixel)
    })

    ASCIIImage(asciiPixels)
  }
}


