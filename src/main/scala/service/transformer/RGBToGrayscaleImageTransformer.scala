package service.transformer

import domain.{RGBImage, GrayscaleImage, RGBPixel, GrayscalePixel}

/**
 * Transformer to convert an RGBImage into a GrayscaleImage.
 */
class RGBToGrayscaleImageTransformer extends RegularToRegularImageTransformer[GrayscaleImage] {

  type Input = RGBImage

  /**
   * Transforms an RGBImage into a GrayscaleImage.
   *
   * @param image the RGBImage to transform
   * @return the resulting GrayscaleImage
   */
  override def transform(image: RGBImage): GrayscaleImage = {
    val grayscalePixels = image.getPoints.map(_.map { rgbPixel =>
      val grayscaleValue = calculateGrayscale(rgbPixel)
      GrayscalePixel(grayscaleValue)
    })

    GrayscaleImage(grayscalePixels)
  }
  
  /**
   * Calculates the grayscale intensity from an RGB pixel.
   * The formula used is the weighted average method for luminance.
   *
   * @param pixel the RGBPixel
   * @return the grayscale intensity value
   */
  private def calculateGrayscale(pixel: RGBPixel): Int = {
    val redWeight = 0.3
    val greenWeight = 0.59
    val blueWeight = 0.11

    (pixel.redValue * redWeight + pixel.greenValue * greenWeight + pixel.blueValue * blueWeight).round.toInt
  }
}
