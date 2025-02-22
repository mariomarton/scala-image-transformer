package view

import controller.exceptions.AppControllerException
import controller.{AppConfig, AppController}
import service.exporter.{ConsoleExporter, FileExporter}

import scala.util.{Failure, Success, Try}


/**
 * Entry point for the ASCII Art app.
 * Parses command-line arguments, initializes the controller, and executes the pipeline.
 *
 * Allowed app arguments:
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
object AsciiArtUI {

  def main(args: Array[String]): Unit = {
    val parser = new CommandLineParser(args)
    val controller = new AppController()

    parser.parseArgs() match {
      case Success(config) =>
        Try(controller.run(config)) match {
          case Success(_) => // Successfully ran the app, no extra output
          case Failure(exception: AppControllerException) => println(s"Error: ${exception.getMessage}")
          case Failure(exception) => println(s"Unexpected error: ${exception.getMessage}") // just in case
        }
      case Failure(exception: IllegalArgumentException) =>
        println(s"Error: ${exception.getMessage}")
      case Failure(exception) =>
        println(s"Unexpected error: ${exception.getMessage}") // just in case
    }
  }
}
