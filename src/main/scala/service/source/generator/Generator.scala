package service.source.generator

import constants.Constants as const
import domain.{CustomImage, RGBImage, RGBPixel}
import service.source.generator.Generator

import scala.util.Random

/**
 * Trait for image generators.
 */
trait Generator[O <: CustomImage[_]] {
  def generateImage(): O
}

/**
 * Generator implementation that creates a random RGBImage.
 * - Dimensions are randomly chosen between reasonable limits.
 * - Each pixel has random RGB values.
 *
 * @param seed Optional seed, if not provided, a random seed is used.
 */
class RandomImageGenerator(seed: Option[Long] = None) extends Generator[RGBImage] {
  private val random = new Random(seed.getOrElse(new Random().nextLong()))

  override def generateImage(): RGBImage = {
    val width = random.between(const.MIN_RANDOM_IMAGE_WIDTH, const.MAX_RANDOM_IMAGE_WIDTH)
    val height = random.between(const.MIN_RANDOM_IMAGE_HEIGHT, const.MAX_RANDOM_IMAGE_HEIGHT)
    val pixels = Seq.tabulate(width, height) { (_, _) =>
      RGBPixel(random.nextInt(256), random.nextInt(256), random.nextInt(256)) // Random RGB values
    }
    RGBImage(pixels)
  }
}


