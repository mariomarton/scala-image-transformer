package testutils

import java.nio.file.{Files, Path}
import java.nio.file.StandardOpenOption.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import scala.util.Random
import domain.{GrayscalePixel, RGBPixel, GrayscaleImage}

// Object with various functions used through-out the testing of the app
object TestUtils {
  /**
   * Deletes a set of files if they exist.
   *
   * @param paths The paths to delete.
   */
  def cleanUpFiles(paths: Path*): Unit = {
    paths.foreach(Files.deleteIfExists)
  }

  /**
   * Creates a new RGB image file at the specified path with a customizable size and format.
   *
   * @param path       The path where the image file will be created.
   * @param formatName The format of the image (default is "jpg").
   * @param width      The width of the image (default is 64 pixels).
   * @param height     The height of the image (default is 64 pixels).
   */
  def createImage(path: Path, formatName: String = "jpg", width: Int = 64, height: Int = 64): Unit = {
    val bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    ImageIO.write(bufferedImage, formatName, path.toFile)
  }

  // Utility method to create a BufferedImage using a 2D Seq of (R, G, B) tuples
  def createBufferedImage(pixels: Seq[Seq[(Int, Int, Int)]]): BufferedImage = {
    val height = pixels.length
    val width = if (height > 0) pixels.head.length else 0
    val bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    pixels.zipWithIndex.foreach { case (row, y) =>
      row.zipWithIndex.foreach { case ((r, g, b), x) =>
        val pixelInt = (r << 16) | (g << 8) | b
        bufferedImage.setRGB(x, y, pixelInt)
      }
    }

    bufferedImage
  }

  def createGrayscaleImage(data: Seq[Seq[Int]]): GrayscaleImage = {
    GrayscaleImage(intsToGrayscalePixels(data))
  }

  // Generic function to generate a 2D Seq of random RGB pixels or grayscale pixels with a fixed seed
  def generateRandomPixelsBase[T](width: Int, height: Int, seed: Int, grayscale: Boolean = false): Seq[Seq[T]] = {
    val random = new Random(seed)
    Seq.fill(height)(Seq.fill(width) {
      if (grayscale) {
        random.nextInt(256).asInstanceOf[T] // Grayscale pixel (single Int)
      } else {
        (random.nextInt(256), random.nextInt(256), random.nextInt(256)).asInstanceOf[T] // RGB pixel (tuple)
      }
    })
  }

  def generateRandomPixels(width: Int, height: Int, seed: Int): Seq[Seq[(Int, Int, Int)]] = {
    generateRandomPixelsBase[(Int, Int, Int)](width, height, seed)
  }

  def generateRandomGrayscalePixels(width: Int, height: Int, seed: Int): Seq[Seq[Int]] = {
    generateRandomPixelsBase[Int](width, height, seed, true)
  }

  // Helper function to convert Seq of (R, G, B) tuples to RGBPixels
  def tuplesToRGBPixels(pixelColors: Seq[Seq[(Int, Int, Int)]]): Seq[Seq[RGBPixel]] = {
    pixelColors.map(_.map { case (r, g, b) => RGBPixel(r, g, b) })
  }

  // Helper function to convert Seq of (Int) to GrayscalePixels
  def intsToGrayscalePixels(pixelColors: Seq[Seq[Int]]): Seq[Seq[GrayscalePixel]] = {
    pixelColors.map(_.map { case (x) => GrayscalePixel(x) })
  }


  /**
   * Converts an RGB pixel (R, G, B) to a grayscale value.
   *
   * @param red   Red component (0-255).
   * @param green Green component (0-255).
   * @param blue  Blue component (0-255).
   * @return The grayscale value using the formula: 0.3 * R + 0.59 * G + 0.11 * B
   */
  def calculateGrayscale(red: Int, green: Int, blue: Int): Int = {
    ((0.3 * red) + (0.59 * green) + (0.11 * blue)).round.toInt
  }

  /**
   * Converts a BufferedImage to a 2D sequence of RGBPixel objects.
   *
   * @param bufferedImage The input BufferedImage.
   * @return 2D sequence of RGBPixel.
   */
  def bufferedImageToRGBPixels(bufferedImage: BufferedImage): Seq[Seq[RGBPixel]] = {
    val height = bufferedImage.getHeight
    val width = bufferedImage.getWidth
    Seq.tabulate(height, width) { (y, x) =>
      val rgb = bufferedImage.getRGB(x, y)
      val red = (rgb >> 16) & 0xFF
      val green = (rgb >> 8) & 0xFF
      val blue = rgb & 0xFF
      RGBPixel(red, green, blue)
    }
  }

}

