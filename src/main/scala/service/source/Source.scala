package service.source

import domain.ImageBase

/**
 * Base trait for sources that retrieve images.
 *
 * @tparam O Output – the type of the object eventually returned by the source
 */
trait Source[O <: ImageBase] {
  def getImage: O
}
