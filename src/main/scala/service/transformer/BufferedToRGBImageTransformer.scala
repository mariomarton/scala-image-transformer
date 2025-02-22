package service.transformer

import java.io.File
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import domain.{RGBImage, RGBPixel, RegularCustomImage, BufferedImageAdapter}

/**
 * Transforms a BufferedImage into an RGBImage.
 */
class BufferedToRGBImageTransformer extends ToRegularImageTransformer[RGBImage] {

  type Input = BufferedImageAdapter

  /**
   * Transforms an image file into RGBImage.
   *
   * @return an instance of RGBImage
   */
  override def transform(BufferedImageAdapter: BufferedImageAdapter): RGBImage = {
    val bufferedImage: BufferedImage = BufferedImageAdapter.getBufferedImage

    val width = bufferedImage.getWidth
    val height = bufferedImage.getHeight

    val pixels = Seq.tabulate(height, width) { (y, x) => // height - outer Seq, width - inner Seq
      val color = bufferedImage.getRGB(x, y)
      val red = (color >> 16) & 0xFF
      val green = (color >> 8) & 0xFF
      val blue = color & 0xFF
      RGBPixel(red, green, blue)
    }

    RGBImage(pixels)
  }
}