package service.transformer.grayscaletoascii

import domain.{ASCIISymbol, GrayscalePixel}

/**
 * Trait for grayscale-to-ASCII tables.
 *
 * Provides a contract for mapping grayscale values to ASCII characters.
 */
trait GrayscaleToASCIITable {

  /**
   * Maps a GrayscalePixel to an ASCIISymbol.
   *
   * @param grayscalePixel the GrayscalePixel
   * @return the corresponding ASCIISymbol
   */
  def getASCIISymbol(grayscalePixel: GrayscalePixel): ASCIISymbol
}

/**
 * Linear grayscale-to-ASCII table.
 * Maps grayscale values to characters in equal ranges.
 */
case class LinearTable(characters: Seq[Char]) extends GrayscaleToASCIITable {
  private val step = 256 / characters.size

  override def getASCIISymbol(grayscalePixel: GrayscalePixel): ASCIISymbol = {
    val index = Math.min(grayscalePixel.value / step, characters.size - 1)
    ASCIISymbol(characters(index))
  }
}

/**
 * Non-linear grayscale-to-ASCII table.
 * Maps grayscale values to characters based on unevenly distributed ranges.
 */
case class NonLinearTable(ranges: Seq[(Int, Char)]) extends GrayscaleToASCIITable {
  override def getASCIISymbol(grayscalePixel: GrayscalePixel): ASCIISymbol = {
    // find the first range where the grayscale value is <= to the upper bound of the range
    val matchingCharacter: Option[Char] = ranges.find { case (upperBound, _) =>
      grayscalePixel.value <= upperBound
    }.map(_._2)

    // handle cases where no range is found,
    // if .find returns None (grayscale value exceeds all upper bounds),
    // default to the character in the last range
    val character: Char = matchingCharacter.getOrElse(ranges.last._2)

    ASCIISymbol(character)
  }
}
