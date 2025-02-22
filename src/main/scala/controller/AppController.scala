package controller

import controller.exceptions.AppControllerException

import java.nio.file.{Files, Path}
import domain.{ASCIIImage, BufferedImageAdapter, GrayscaleImage, ImageBase, Point, RGBImage, RegularCustomImage}
import service.exceptions.exporter.ExportException
import service.exceptions.source.SourceException
import service.exporter.{ConsoleExporter, Exporter, FileExporter}
import service.source.Source
import service.transformer.{BufferedToRGBImageTransformer, RGBToGrayscaleImageTransformer}
import service.transformer.filter.{Filter, GrayscaleFilter, InvertFilter, RotateFilter, ScaleFilter}
import service.transformer.grayscaletoascii.{GrayscaleToASCIIAlgorithm, GrayscaleToASCIIImageTransformer, GrayscaleToASCIITable, TableBasedGrayscaleToASCIIAlgorithm}

/**
 * Configuration class encapsulating all necessary data for running the app
 */
case class AppConfig(
                      source: Source[_ <: ImageBase],
                      filters: List[Filter[_ <: RegularCustomImage[_ <: Point]]],
                      table: GrayscaleToASCIITable,
                      exporters: List[Exporter],
                    ) {
  override def toString: String = {
    s"AppConfig(" +
      s"source=${source.getClass.getSimpleName}, " +
      s"filters=${filters.map(_.getClass.getSimpleName).mkString(", ")}, " +
      s"table=${table.getClass.getSimpleName}, " +
      s"exporters=${exporters.map(_.getClass.getSimpleName).mkString(", ")}" +
      ")"
  }
}

class AppController {

  /**
   * Run the ASCII converter app with the specified configuration.
   *
   * @param config An instance of AppConfig containing input/output details and processing options.
   * @throws AppControllerException if import or export fails
   */
  def run(config: AppConfig): Unit = {

    // Get image from the source (or throw AppControllerException if import fails)
    val imageFromSource = try {
      config.source.getImage
    } catch {
      case e: SourceException => throw AppControllerException(e.getMessage, e)
    }

    // Transform it to a grayscale image
    val grayscaleImage = sourceImageToGrayscaleImage(imageFromSource)

    // Apply filters designed for GrayscaleImage
    // (in this version of the app, all filters are done in this stage,
    // but could easily be changed to also include filters in other stages)
    val filteredImage = applyGrayscaleFilters(grayscaleImage, config.filters)

    // Convert to ASCII
    val algorithm: GrayscaleToASCIIAlgorithm = TableBasedGrayscaleToASCIIAlgorithm(config.table)
    val asciiImage = new GrayscaleToASCIIImageTransformer(algorithm).transform(filteredImage)

    // Export to all destinations (or throw AppControllerException if export fails)
    config.exporters.foreach { exporter =>
      try{
        exporter.exportASCII(asciiImage)
      } catch {
        case e: ExportException => throw AppControllerException(e.getMessage, e)
      }
    }
  }

  /**
   * Transforms source image to GrayscaleImage.
   *
   * @param image An instance of ImageBase from the source.
   * @return A fully transformed GrayscaleImage.
   */
  private def sourceImageToGrayscaleImage(image: ImageBase): GrayscaleImage = {
    // This is done because Source can produce different types of images,
    // so we don't know where in the conversion pipeline we need to start
    image match {
      case bufferedImage: BufferedImageAdapter =>
        val rgbImage = new BufferedToRGBImageTransformer().transform(bufferedImage)
        new RGBToGrayscaleImageTransformer().transform(rgbImage)
      case rgbImage: RGBImage =>
        new RGBToGrayscaleImageTransformer().transform(rgbImage)
      case grayscaleImage: GrayscaleImage =>
        grayscaleImage // Already in the desired state
      case _ =>
        throw new AppControllerException("Unsupported image type returned by Source.")
    }
  }

  /**
   * Apply filters designed for GrayscaleImage.
   *
   * @param image   The input GrayscaleImage.
   * @param filters The list of filters to apply.
   * @return The filtered GrayscaleImage.
   */
  private def applyGrayscaleFilters(image: GrayscaleImage, filters: List[Filter[_]]): GrayscaleImage = {
    filters.collect { case filter: GrayscaleFilter => filter }
      .foldLeft(image) { (currentImage, filter) =>
        filter.transform(currentImage)
      }
  }
}