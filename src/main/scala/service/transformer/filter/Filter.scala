package service.transformer.filter

import domain.{ASCIIImage, GrayscaleImage, Point, RGBImage, RegularCustomImage}
import service.transformer.RegularToRegularImageTransformer

/**
 * Trait defining Filter, which is a transformer with the same input and output image type.
 *
 * @tparam T input and output image type
 */
trait Filter[T <: RegularCustomImage[_ <: Point]] extends RegularToRegularImageTransformer[T]{

  type Input = T

  /**
   * Transform an input of type T to output of (also) type T.
   *
   * @param input the input, which must be a subtype of RegularCustomImage
   * @return the transformed image
   */
  def transform(input: Input): T
}

// RGBFilters are meant to be applied after transformation to RGB and before transformation to grayscale
trait RGBFilter extends Filter[RGBImage]

// GrayscaleFilters are meant to be applied after transformation to grayscale and before transformation to ASCIIImage
trait GrayscaleFilter extends Filter[GrayscaleImage]

// ASCIIFilters are meant to be applied after transformation to ASCIIImage
trait ASCIIFilter extends Filter[ASCIIImage]