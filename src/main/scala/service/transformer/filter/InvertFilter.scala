package service.transformer.filter

import constants.Constants.WHITE
import domain.GrayscaleImage

class InvertFilter extends GrayscaleFilter {
  override def transform(input: GrayscaleImage): GrayscaleImage = {
    val invertedPoints = input.getPoints.map(_.map(_.invert))
    GrayscaleImage(invertedPoints)
  }
}
