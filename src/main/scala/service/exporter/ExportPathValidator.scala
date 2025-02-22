package service.exporter

import service.exceptions.exporter.ExportPathValidationException

import java.nio.file.{Files, Path}

/**
 * A trait for export file path validator, that makes sure the path for exporting to a file is okay.
 */
trait ExportPathValidator {
  /**
   * Validates the given file path to ensure it meets all necessary requirements.
   *
   * @param filePath the file path to validate.
   * @throws ExportPathValidationException if the validation fails.
   */
  @throws[ExportPathValidationException]
  def validatePath(filePath: Path): Unit
}


/**
 * Concrete implementation of the ExportPathValidator.
 */
object DefaultExportPathValidator extends ExportPathValidator {

  /**
   * Validates the given file path.
   *
   * @param filePath the file path to validate.
   * @throws ExportPathValidationException if the validation fails.
   */
  override def validatePath(filePath: Path): Unit = {
    val parentDir = filePath.getParent
    if (parentDir != null && !Files.exists(parentDir)) {
      throw new ExportPathValidationException(s"Directory does not exist: ${parentDir.toAbsolutePath}")
    }
    if (parentDir != null && !Files.isWritable(parentDir)) {
      throw new ExportPathValidationException(s"Directory is not writable: ${parentDir.toAbsolutePath}")
    }
    if (Files.exists(filePath) && !Files.isWritable(filePath)) {
      throw new ExportPathValidationException(s"File is not writable: ${filePath.toAbsolutePath}")
    }
  }
}