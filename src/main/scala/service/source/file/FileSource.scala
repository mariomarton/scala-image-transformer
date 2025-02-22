package service.source.file

import java.io.IOException
import java.nio.file.Path
import javax.imageio.ImageIO

import constants.Constants as const
import domain.{BufferedImageAdapter, ImageBase}
import service.exceptions.source.{FileNotFoundException, ImageReadException, InvalidImageExtensionException, InvalidImageFileException}
import service.source.Source


/**
 * Trait for file-based sources.
 *
 * @tparam O Output – the type of the object eventually returned by the source
 */
trait FileSource[O <: ImageBase] extends Source[O] {

  val supportedExtensions: Set[String]
  val filePath: Path
  val validator: FileValidator

  /**
   * Retrieves the image from the file path.
   *
   * @return the image instance
   * @throws FileNotFoundException        if the file doesn't exist or path is null
   * @throws InvalidImageExtensionException  if the file has an unsupported extension
   * @throws InvalidImageFileException    if the image file is invalid or corrupted
   * @throws ImageReadException           if an error occurs while reading the image
   */
  override def getImage: O = {
    validator.validate(filePath, supportedExtensions)
    loadImageFromFile()
  }

  /**
   * Reads the image from the file path.
   * @throws ImageReadException        if an error occurs while reading the image
   * @return the image instance
   */
  protected def loadImageFromFile(): O
}


/**
 * A source for standard image files.
 */
class StandardFileSource(
                          override val filePath: Path,
                          override val validator: FileValidator = new StandardFileValidator // Default implementation
                        ) extends FileSource[BufferedImageAdapter] {

  override val supportedExtensions: Set[String] = const.STANDARD_FILE_SOURCE_SUPPORTED_EXTENSIONS

  /**
   * Reads the image as a BufferedImage using ImageIO.
   *
   * @return the BufferedImage instance
   * @throws InvalidImageFileException if the image file is invalid or corrupted
   * @throws ImageReadException        if an error occurs while reading the image
   */
  override def loadImageFromFile(): BufferedImageAdapter = {
    try {
      val image = ImageIO.read(filePath.toFile)
      if (image == null) {
        throw new InvalidImageFileException(s"Invalid or corrupted image file: $filePath")
      }
      BufferedImageAdapter(image)
    } catch {
      case e: IOException =>
        throw new ImageReadException(s"An error occurred while reading the image file: $filePath", e)
    }
  }
}