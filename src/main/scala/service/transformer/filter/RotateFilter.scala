package service.transformer.filter

import domain.{GrayscalePixel, GrayscaleImage}

trait RotateFilter extends GrayscaleFilter {
  val degrees: Int
}

class SimpleRotateFilter(override val degrees: Int) extends RotateFilter {

  // Normalise the degrees to [0, 360)
  private val normalisedDegrees = ((degrees % 360) + 360) % 360

  // Define valid multiples of 90
  private val validDegrees = Seq(0, 90, 180, 270)

  // Ensure only valid multiples of 90 are provided
  require(validDegrees.contains(normalisedDegrees), "Only rotations that are multiples of 90 degrees are supported.")

  override def transform(input: GrayscaleImage): GrayscaleImage = {
    val rotatedPoints = rotate(input.getPoints, normalisedDegrees)

    GrayscaleImage(rotatedPoints)
  }

  private def rotate(points: Seq[Seq[GrayscalePixel]], degrees: Int): Seq[Seq[GrayscalePixel]] = degrees match {
    case 0    => points
    case 90   => rotate90(points)
    case 180  => rotate180(points)
    case 270  => rotate90(points, clockwise = false)
  }

  private def rotate90(points: Seq[Seq[GrayscalePixel]], clockwise: Boolean = true): Seq[Seq[GrayscalePixel]] = {
    // Transposing the matrix swaps rows with columns. For clockwise rotation, reverse each row after transposing.
    if (clockwise)
      points.transpose.map(_.reverse)
    // For counterclockwise rotation, reverse the entire transposed matrix.
    else
      points.transpose.reverse
  }

  private def rotate180(points: Seq[Seq[GrayscalePixel]]): Seq[Seq[GrayscalePixel]] = {
    // Reversing the entire seq of rows mirrors the matrix vertically.
    // Then, reversing each individual row mirrors each row horizontally.
    // This combination results in 180 degree rotation.
    points.reverse.map(_.reverse)
  }
}
