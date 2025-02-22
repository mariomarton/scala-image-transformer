package service.transformer

import domain.{CustomImage, ImageBase, RegularCustomImage}

/**
 * Trait defining the contract for image transformations.
 * @tparam O the output type of the transformation
 */
trait Transformer[O] {
  type Input <: ImageBase // type member representing the input type
  def transform(input: Input): O
}

/**
 * Trait defining the contract for transformations from any ImageBase-based image to CustomImage-based image.
 * In simple words, with this transformer we could transform for example BufferedImageAdapter to RGBImage.
 * @tparam O the output type for the transformation (must be based on CustomImage)
 */
trait ToImageTransformer[O <: CustomImage[_]] extends Transformer[O]

/**
 * Trait defining the contract for transformations that return regular images.
 * @tparam O the output type for the transformation (must be based on RegularCustomImage)
 */
trait ToRegularImageTransformer[O <: RegularCustomImage[_]] extends ToImageTransformer[O]

/**
 * Trait defining the contract for transformations that involve a regular image as input and output.
 * @tparam O the output type for the transformation (must be based on RegularCustomImage)
 */
trait RegularToRegularImageTransformer[O <: RegularCustomImage[_]] extends ToRegularImageTransformer[O] {

  type Input <: RegularCustomImage[_]

  /**
   * Transform an input of type RegularCustomImage to another RegularCustomImage type.
   * @param input the input, which must be a subtype of RegularCustomImage
   * @return the transformed image
   */
  def transform(input: Input): O
}
