package domain

import service.exporter.Exporter

import collection.immutable.Seq // Scala seems to default this anyway, but just in case, we want immutable Seq

trait ImageBase

// Wrapper for BufferedImage since the App works with custom Images and also BufferedImage images
class BufferedImageAdapter(val bufferedImage: java.awt.image.BufferedImage) extends ImageBase {
  def getBufferedImage: java.awt.image.BufferedImage = bufferedImage
}

/**
 * Trait representing the base functionality of an image.
 *
 * A generic `CustomImage` that operates on a two-dimensional seq of points.
 * The points are of type `P`, `P` must extend the `Point` class.
 */
trait CustomImage[P <: Point] extends ImageBase {

  def points: Seq[Seq[P]]

  def getDimensions: (Int, Int) = {
    val width = points.length
    val height = if (points.nonEmpty) points.head.length else 0
    (width, height)
  }

  def getPoint(x: Int, y: Int): P = points(x)(y)

  def getPoints: Seq[Seq[P]] = points
}

/**
 * A specialized trait extending `CustomImage` for "regular "images,
 * meaning those with the same number of points in every row.
 */
trait RegularCustomImage[P <: Point] extends CustomImage[P] {}

case class ASCIIImage(points: Seq[Seq[ASCIISymbol]])
  extends RegularCustomImage[ASCIISymbol]{
}

case class RGBImage(points: Seq[Seq[RGBPixel]])
  extends RegularCustomImage[RGBPixel]{}

case class GrayscaleImage(points: Seq[Seq[GrayscalePixel]])
  extends RegularCustomImage[GrayscalePixel]{}