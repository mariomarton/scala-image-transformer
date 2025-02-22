package service.exporter

import java.io.PrintWriter
import java.nio.file.{Files, Path}
import scala.util.{Failure, Success, Try, Using}
import domain.ASCIIImage
import service.exceptions.exporter.{ConsoleExportException, ExportPathValidationException, FileExportException}

/**
 * Base Exporter Trait.
 */
trait Exporter {
  /**
   * Exports the given ASCII image.
   *
   * @param image the ASCII image to export.
   */
  def exportASCII(image: ASCIIImage): Unit
}

/**
 * An exporter implementation that prints ASCII images to the console.
 */
class ConsoleExporter extends Exporter {

  /**
   * Exports the given ASCII image to the console.
   *
   * @param image the ASCII image to export.
   * @throws ConsoleExportException if an error occurs while printing to the console.
   */
  override def exportASCII(image: ASCIIImage): Unit = {
    try {
      image.points.foreach { row =>
        println(row.map(_.value).mkString("")) // Extract and print the `value` of each ASCIISymbol
      }
    } catch {
      case e: Exception =>
        throw new ConsoleExportException("Failed to print ASCII image to the console.", e)
    }
  }
}

/**
 * An exporter implementation that writes ASCII images to a file.
 *
 * @param filePath the path of the file to write the ASCII image to.
 * @param validator the ExportPathValidator validator to validate the file path. Defaults to DefaultExportPathValidator.
 */
class FileExporter(val filePath: Path, validator: ExportPathValidator = DefaultExportPathValidator) extends Exporter {

  /**
   * Exports the given ASCII image to a file at the specified path.
   *
   * @param image the ASCII image to export.
   * @throws ExportPathValidationException if the file path validation fails.
   * @throws FileExportException           if an I/O error or an unexpected error happens during export.
   */
  override def exportASCII(image: ASCIIImage): Unit = {
    try {
      // Validate the file path using the provided validator
      validator.validatePath(filePath)

      // Write the ASCII image to the file
      Using(new PrintWriter(Files.newBufferedWriter(filePath))) { writer =>
        image.points.foreach { row =>
          writer.println(row.map(_.value).mkString("")) // Write each row of the image
        }
      }
    } catch {
      case e: ExportPathValidationException =>
        throw e
      case e: java.io.IOException =>
        throw new FileExportException(s"Failed to write to file: ${filePath.toAbsolutePath}", e)
      case e: Throwable =>
        throw new FileExportException(s"An unexpected error occurred.", e)
    }
  }
}
