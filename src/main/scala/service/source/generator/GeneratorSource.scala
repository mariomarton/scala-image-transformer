package service.source.generator

import domain.CustomImage
import service.source.Source

/**
 * A source for generated RGB images.
 *
 * @tparam O Output – the type of image eventually returned by the source (must inherit from CustomImage)
 */
class GeneratorSource[O <: CustomImage[_]](generator: Generator[O]) extends Source[O] {

  /**
   * Generates an image of type O using the provided generator.
   *
   * @return the generated image
   */
  override def getImage: O = generator.generateImage()
}
