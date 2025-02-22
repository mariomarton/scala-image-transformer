package service.transformer.filter

import domain.{Point, GrayscalePixel, GrayscaleImage, RegularCustomImage}

trait ScaleFilter[T <: RegularCustomImage[_ <: Point]] extends Filter[T] {
  val factor: Double
}

class SimpleScaleFilter(override val factor: Double) extends ScaleFilter[GrayscaleImage] with GrayscaleFilter {
  // Define valid scale factors
  private val validFactors = Seq(0.25, 1.0, 4.0)

  // Ensure the scale is one of the valid values
  require(validFactors.contains(factor), "Only scale factors of 0.25, 1 and 4 are supported.")

  override def transform(input: GrayscaleImage): GrayscaleImage = {
    factor match {
      case 1.0 => input // Return the original image
      case _   => scale(input)
    }
  }

  private def scale(image: GrayscaleImage): GrayscaleImage = {
    val points = image.getPoints
    val (originalRows, originalCols) = image.getDimensions

    factor match {
      case 0.25 => scaleDown(points, originalRows, originalCols)
      case 4.0  => scaleUp(points, originalRows, originalCols)
    }
  }

  // Helper method for scaling by 0.25: Collapse 2x2 blocks into one symbol by averaging pixel values
  private def scaleDown(points: Seq[Seq[GrayscalePixel]], originalRows: Int, originalCols: Int): GrayscaleImage = {
    val scaledPoints = for (y <- 0 until originalRows by 2) yield {
      val collapsedRow = for (x <- 0 until originalCols by 2) yield {
        val avgValue = averageGrayscaleValue(points, y, x)
        GrayscalePixel(avgValue.round.toInt) // Create a new GrayscalePixel with the average value
      }
      collapsedRow
    }

    GrayscaleImage(scaledPoints)
  }

  // Helper method for scaling by 4: Expand each pixel into a 2x2 block
  private def scaleUp(points: Seq[Seq[GrayscalePixel]], originalRows: Int, originalCols: Int): GrayscaleImage = {
    val scaledPoints = points.flatMap { row =>
      Seq.fill(2) {
        row.flatMap(pixel => Seq.fill(2)(pixel)) // Expand each pixel into a 2x2 block
      }
    }
    GrayscaleImage(scaledPoints)
  }
  
  // Helper function to calculate the average grayscale value for a 2x2 block
  private def averageGrayscaleValue(points: Seq[Seq[GrayscalePixel]], row: Int, col: Int): Double = {
    val values = for {
      y <- row until (row + 2) if y < points.size
      x <- col until (col + 2) if x < points(y).size
    } yield points(y)(x).value

    // Compute the average of the values in the 2x2 block
    if (values.nonEmpty) values.sum / values.size else 0.0
  }
}
