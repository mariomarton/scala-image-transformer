package view

import controller.AppConfig
import domain.RGBImage
import org.scalatest.funsuite.AnyFunSuite
import service.exporter.{ConsoleExporter, FileExporter}
import service.source.file.StandardFileSource
import service.source.generator.{GeneratorSource, RandomImageGenerator}
import service.transformer.filter.{InvertFilter, SimpleRotateFilter, SimpleScaleFilter}
import service.transformer.grayscaletoascii.GrayscaleToASCIIUtilities

import java.nio.file.Paths
import scala.util.{Failure, Success, Try}

/**
 * Unit tests for the CommandLineParser class.
 */
class CommandLineParserTests extends AnyFunSuite {

  /**
   * Helper method to create a parser and execute parsing.
   * @param args Array of arguments.
   * @return Try[AppConfig] returned by CommandLineParser.
   */
  private def parseArguments(args: Array[String]): Try[AppConfig] = {
    val parser = new CommandLineParser(args)
    parser.parseArgs()
  }

  // test parsing: Source
  test("CommandLineParserTests creates AppConfig with a valid file source") {
    val args = Array("--image", "test_image.png", "--output-console")
    val result = parseArguments(args)

    assert(result.isSuccess)
    result.foreach { config =>
      assert(config.source.isInstanceOf[StandardFileSource])
      assert(config.exporters.exists(_.isInstanceOf[ConsoleExporter]))
    }
  }

  test("CommandLineParserTests creates AppConfig with random image source") {
    val args = Array("--image-random", "--output-console")
    val result = parseArguments(args)

    assert(result.isSuccess)
    result.foreach { config =>
      assert(config.source.isInstanceOf[GeneratorSource[_]])
      assert(config.exporters.exists(_.isInstanceOf[ConsoleExporter]))
    }
  }

  test("CommandLineParserTests throws exception when both image and image-random are specified") {
    val args = Array("--image", "test_image.png", "--image-random")
    val result = parseArguments(args)

    assert(result.isFailure)
    assert(result.failed.get.isInstanceOf[IllegalArgumentException])
  }

  test("CommandLineParserTests throws exception when no source (--image*) is specified") {
    val args = Array("--output-console")
    val result = parseArguments(args)

    assert(result.isFailure)
    assert(result.failed.get.isInstanceOf[IllegalArgumentException])
  }

  // test parsing: Filters
  test("CommandLineParserTests correctly parses filters") {
    val args = Array("--image", "test_image.png", "--rotate", "90", "--scale", "0.25", "--invert", "--output-console")
    val result = parseArguments(args)

    assert(result.isSuccess)
    result.foreach { config =>
      assert(config.filters.exists(_.isInstanceOf[SimpleRotateFilter]))
      assert(config.filters.exists(_.isInstanceOf[SimpleScaleFilter]))
      assert(config.filters.exists(_.isInstanceOf[InvertFilter]))
    }
  }

  test("CommandLineParserTests throws exception for invalid rotation value") {
    val args = Array("--image", "test_image.png", "--rotate", "invalid", "--output-console")
    val result = parseArguments(args)

    assert(result.isFailure)
    assert(result.failed.get.isInstanceOf[NumberFormatException])
  }

  test("CommandLineParser handles '--rotate' with negative numbers") {
    val parser = new CommandLineParser(Array("--image", "path/to/image.png", "--rotate", "-90", "--output-console"))
    val config = parser.parseArgs().get

    val rotateFilter = config.filters.collect { case f: SimpleRotateFilter => f }
    assert(rotateFilter.nonEmpty)
    assert(rotateFilter.head.degrees == -90)
  }

  test("CommandLineParser handles '--rotate' with positive numbers prefixed by '+'") {
    val parser = new CommandLineParser(Array("--image", "path/to/image.png", "--rotate", "+90", "--output-console"))
    val config = parser.parseArgs().get

    val rotateFilter = config.filters.collect { case f: SimpleRotateFilter => f }
    assert(rotateFilter.nonEmpty)
    assert(rotateFilter.head.degrees == 90)
  }

  test("CommandLineParser handles multiple '--invert' filters") {
    val parser = new CommandLineParser(Array("--image", "path/to/image.png", "--invert", "--invert", "--output-console"))
    val config = parser.parseArgs().get

    val invertFilters = config.filters.collect { case f: InvertFilter => f }
    assert(invertFilters.size == 2)
  }

  test("CommandLineParser handles multiple '--scale' filters") {
    val parser = new CommandLineParser(Array("--image", "path/to/image.png", "--scale", "4", "--scale", "0.25", "--output-console"))
    val config = parser.parseArgs().get

    val scaleFilters = config.filters.collect { case f: SimpleScaleFilter => f }
    assert(scaleFilters.size == 2)
    assert(scaleFilters.exists(_.factor == 4.0))
    assert(scaleFilters.exists(_.factor == 0.25))
  }

  test("CommandLineParser handles multiple '--rotate' filters") {
    val parser = new CommandLineParser(Array("--image", "path/to/image.png", "--rotate", "90", "--rotate", "-90", "--output-console"))
    val config = parser.parseArgs().get

    val rotateFilters = config.filters.collect { case f: SimpleRotateFilter => f }
    assert(rotateFilters.size == 2)
    assert(rotateFilters.exists(_.degrees == 90))
    assert(rotateFilters.exists(_.degrees == -90))
  }

  // test parsing: Table
  test("CommandLineParserTests correctly handles built-in grayscale-to-ASCII tables") {
    val args = Array("--image", "test_image.png", "--table", "linear", "--output-console")
    val result = parseArguments(args)

    assert(result.isSuccess)
    result.foreach { config =>
      assert(config.table == GrayscaleToASCIIUtilities.defaultLinearTable())
    }
  }

  test("CommandLineParserTests throws exception for invalid table name") {
    val args = Array("--image", "test_image.png", "--table", "unknown", "--output-console")
    val result = parseArguments(args)

    assert(result.isFailure)
    assert(result.failed.get.isInstanceOf[IllegalArgumentException])
  }

  test("CommandLineParserTests correctly handles custom grayscale-to-ASCII table") {
    val args = Array("--image", "test_image.png", "--custom-table", "@#$%", "--output-console")
    val result = parseArguments(args)

    assert(result.isSuccess)
    result.foreach { config =>
      assert(config.table == GrayscaleToASCIIUtilities.getLinearTable("@#$%"))
    }
  }

  test("CommandLineParserTests throws exception for empty custom table") {
    val args = Array("--image", "test_image.png", "--custom-table", "", "--output-console")
    val result = parseArguments(args)

    assert(result.isFailure)
    assert(result.failed.get.isInstanceOf[IllegalArgumentException])
  }

  // test parsing: Exporter
  test("CommandLineParser uses (only) console exporter when no exporter is specified") {
    val args = Array("--image", "test_image.png")
    val result = parseArguments(args)

    assert(result.isSuccess)
    val config = result.get

    val consoleExporters = config.exporters.collect { case e: ConsoleExporter => e }
    assert(consoleExporters.size == 1)
  }

  test("CommandLineParserTests correctly handles file exporter") {
    val args = Array("--image", "test_image.png", "--output-file", "output.txt")
    val result = parseArguments(args)

    assert(result.isSuccess)
    result.foreach { config =>
      assert(config.exporters.exists(_.isInstanceOf[FileExporter]))
    }
  }

  test("CommandLineParser does not export to console when only file exporter is specified") {
    val parser = new CommandLineParser(Array("--image", "path/to/image.png", "--output-file", "output.txt"))
    val config = parser.parseArgs().get

    val exporters = config.exporters
    assert(exporters.exists(_.isInstanceOf[FileExporter]))
    assert(!exporters.exists(_.isInstanceOf[ConsoleExporter]))
  }


  test("CommandLineParser allows both file and console exporters simultaneously") {
    val parser = new CommandLineParser(Array("--image", "path/to/image.png", "--output-console", "--output-file", "output.txt"))
    val config = parser.parseArgs().get

    val exporters = config.exporters
    assert(exporters.exists(_.isInstanceOf[ConsoleExporter]))
    assert(exporters.exists(e => e.isInstanceOf[FileExporter] && e.asInstanceOf[FileExporter].filePath.toString == "output.txt"))
  }
}
