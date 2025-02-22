package service.source.file

import service.exceptions.source.{FileNotFoundException, InvalidImageExtensionException}

import java.nio.file.{Files, Path}

/**
 * Base trait for file validators.
 */
trait FileValidator {
  /**
   * Checks if the file path and file extension are valid.
   *
   * @param filePath the file path
   * @param supportedExtensions the supported file extensions
   * @throws FileNotFoundException if the file doesn't exist or path is null
   * @throws InvalidImageExtensionException if the file has an unsupported extension
   */
  @throws[FileNotFoundException]
  @throws[InvalidImageExtensionException]
  def validate(filePath: Path, supportedExtensions: Set[String]): Unit
}
/**
 * Concrete implementation of FileValidator.
 */
class StandardFileValidator extends FileValidator {

  override def validate(filePath: Path, supportedExtensions: Set[String]): Unit = {
    
    // Validates the Path and the extension.
    
    if (filePath == null || !Files.exists(filePath)) {
      throw new FileNotFoundException(s"File not found or path is null: $filePath")
    }

    // Validate the file extension
    val extension = getFileExtension(filePath)
    if (!supportedExtensions.contains(extension.toLowerCase)) {
      throw new InvalidImageExtensionException(
        s"Unsupported file extension: $extension. Supported extensions are: ${supportedExtensions.mkString(", ")}"
      )
    }
  }

  /**
   * Extracts the file extension from the given Path.
   *
   * @param filePath the file path
   * @return the file extension (e.g., "jpg"), or an empty string if no extension is found
   */
  private def getFileExtension(filePath: Path): String = {
    val fileName = filePath.getFileName.toString
    val dotIndex = fileName.lastIndexOf(".")
    if (dotIndex == -1 || dotIndex == fileName.length - 1) "" else fileName.substring(dotIndex + 1)
  }
}
