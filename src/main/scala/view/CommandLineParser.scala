package view

import controller.{AppController, AppConfig}
import service.exporter.{ConsoleExporter, Exporter, FileExporter}
import service.source.Source
import service.transformer.filter.{Filter, InvertFilter, RotateFilter, ScaleFilter, SimpleRotateFilter, SimpleScaleFilter}
import service.transformer.grayscaletoascii.{GrayscaleToASCIITable, GrayscaleToASCIIUtilities}
import domain.{ImageBase, RGBImage}
import service.source.file.StandardFileSource
import service.source.generator.{GeneratorSource, RandomImageGenerator}

import java.nio.file.Paths
import scala.util.{Failure, Success, Try}

/**
 * CommandLineParser is responsible for parsing command-line arguments into an AppConfig instance.
 *
 * Allowed arguments:
 * --image PATH              : Specify the path to the image file.
 * --image-random            : Generate a random image as the source.
 * --rotate NUMBER           : Apply rotation by the specified degrees.
 * --scale NUMBER            : Apply scaling by the specified factor.
 * --invert                  : Apply an inversion filter to the image.
 * --output-console          : Export the output to the console. (Default, if no output arg passed.)
 * --output-file PATH        : Export the output to a file at the given path.
 * --table TABLE_NAME        : Specify a grayscale-to-ASCII conversion table.
 * --custom-table CHARACTERS : Specify a custom grayscale-to-ASCII table using characters.
 *
 * Allowed TABLE_NAME values : linear, non-linear
 */
class CommandLineParser(args: Array[String]) {

  /**
   * Parses the provided arguments into an AppConfig instance.
   *
   * @return AppConfig (wrapped in a Try).
   */
  def parseArgs(): Try[AppConfig] = Try {
    val source = parseSource()
    val filters = parseFilters()
    val exporters = parseExporters()
    val conversionTable = parseConversionTable()

    AppConfig(
      source = source,
      filters = filters,
      table = conversionTable,
      exporters = exporters
    )
  }

  private def parseSource(): Source[_ <: ImageBase] = {
    val imagePath = getArgumentValue("--image")
    val isRandom = containsFlag("--image-random")

    (imagePath, isRandom) match {
      case (Some(path), false) => new StandardFileSource(Paths.get(path))
      case (None, true)        => new GeneratorSource[RGBImage](new RandomImageGenerator())
      case (Some(_), true)     => throw new IllegalArgumentException("Cannot specify both --image and --image-random.")
      case (None, false)       => throw new IllegalArgumentException("You must specify exactly one source: --image or --image-random.")
    }
  }

  private def parseFilters(): List[Filter[_]] = {
    val rotateFilters = getAllArgumentValues("--rotate").map(value => SimpleRotateFilter(value.toInt))
    val scaleFilters = getAllArgumentValues("--scale").map(value => SimpleScaleFilter(value.toDouble))
    val invertFilters = List.fill(args.count(_ == "--invert"))(InvertFilter())

    rotateFilters ++ scaleFilters ++ invertFilters
  }

  private def parseExporters(): List[Exporter] = {
    val consoleExporter = if (containsFlag("--output-console")) Some(new ConsoleExporter) else None
    val fileExporter = getArgumentValue("--output-file").map(path => new FileExporter(Paths.get(path)))

    val exporters = List(consoleExporter, fileExporter).flatten

    if (exporters.isEmpty) {
        // If no exporter arg passed, output to console.
        return List(new ConsoleExporter)
    }

    exporters
  }

  private def parseConversionTable(): GrayscaleToASCIITable = {
    if (containsFlag("--table") && containsFlag("--custom-table")) {
      throw new IllegalArgumentException("Cannot define both --table and --custom-table. Please specify only one.")
    }

    getArgumentValue("--table") match {
      case Some("linear") => GrayscaleToASCIIUtilities.defaultLinearTable()
      case Some("non-linear") => GrayscaleToASCIIUtilities.defaultNonLinearTable()
      case Some(_) =>
        throw new IllegalArgumentException("Unknown table. Allowed names for built-in tables are: linear, non-linear.")
      case None =>
        getArgumentValue("--custom-table") match {
          case None if containsFlag("--custom-table") =>
            throw new IllegalArgumentException("Custom table character set must not be empty.")
          case None => GrayscaleToASCIIUtilities.defaultLinearTable()
          case Some(customTable) if customTable.isEmpty =>
            throw new IllegalArgumentException("Custom table character set must not be empty.")
          case Some(customTable) => GrayscaleToASCIIUtilities.getLinearTable(customTable)
        }
    }
  }

  private def containsFlag(flag: String): Boolean = {
    args.contains(flag)
  }

  /**
   * Helper function to check if an argument exists with a value (where the value doesn't start with '--').
   *
   * @param arg The argument to look for (e.g., "--something").
   * @return An Option[String]:
   *         - Some(value) if the flag exists and has a valid value,
   *         - None if no valid value is found.
   */
  private def getArgumentValue(arg: String): Option[String] = {
    val index = args.indexOf(arg)

    if (index != -1 && index + 1 < args.length && !args(index + 1).startsWith("--")) {
      Some(args(index + 1))
    } else {
      None
    }
  }

  /**
   * Helper function to get all values for a specific argument in the command-line arguments.
   *
   * @param arg The argument to look for (e.g., "--something").
   * @return A list of values associated with the arg.
   */
  private def getAllArgumentValues(arg: String): List[String] = {
    args.sliding(2, 1).collect {
      case Array(`arg`, value) if !value.startsWith("--") => value
    }.toList
  }

}
