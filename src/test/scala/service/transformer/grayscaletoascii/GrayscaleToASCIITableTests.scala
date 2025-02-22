package service.transformer.grayscaletoascii

import domain.{ASCIISymbol, GrayscalePixel}
import domain.{ASCIISymbol, GrayscalePixel}
import org.scalatest.funsuite.AnyFunSuite

/**
 * Base test class for GrayscaleToASCIITable.
 */
abstract class GrayscaleToASCIITableTestBase extends AnyFunSuite {

  def createTable(): GrayscaleToASCIITable

  def tableName: String = createTable().getClass.getSimpleName

  test(s"$tableName maps minimum grayscale value (0) to a valid ASCII symbol") {
    val table = createTable()
    val result = table.getASCIISymbol(GrayscalePixel(0))
    assert(result.isInstanceOf[ASCIISymbol])
  }

  test(s"$tableName maps maximum grayscale value (255) to a valid ASCII symbol") {
    val table = createTable()
    val result = table.getASCIISymbol(GrayscalePixel(255))
    assert(result.isInstanceOf[ASCIISymbol])
  }

  test(s"$tableName maps out-of-range grayscale values correctly") {
    val table = createTable()

    // Below minimum
    val belowMin = table.getASCIISymbol(GrayscalePixel(-1))
    assert(belowMin == table.getASCIISymbol(GrayscalePixel(0)))

    // Above maximum
    val aboveMax = table.getASCIISymbol(GrayscalePixel(256))
    assert(aboveMax == table.getASCIISymbol(GrayscalePixel(255)))
  }

  test(s"$tableName maps mid-range grayscale values to valid ASCII symbols") {
    val table = createTable()

    // Test some mid-range grayscale values
    val midValues = Seq(32, 128, 222)
    midValues.foreach { value =>
      val result = table.getASCIISymbol(GrayscalePixel(value))
      assert(result.isInstanceOf[ASCIISymbol], s"Value $value did not return a valid ASCIISymbol")
    }
  }
}

/**
 * Test suite for LinearTable.
 */
class LinearTableTest extends GrayscaleToASCIITableTestBase {

  override def createTable(): GrayscaleToASCIITable = {
    LinearTable(Seq(' ', '.', ':', '-', '=', '+', '*', '#', '%', '@'))
  }

  test("LinearTable maps edge-case grayscale values to the correct ASCII Symbol") {
    val table = createTable()

    assert(table.getASCIISymbol(GrayscalePixel(24)) == ASCIISymbol(' '))
    assert(table.getASCIISymbol(GrayscalePixel(25)) == ASCIISymbol('.'))
    assert(table.getASCIISymbol(GrayscalePixel(49)) == ASCIISymbol('.'))
    assert(table.getASCIISymbol(GrayscalePixel(50)) == ASCIISymbol(':'))
  }
}

/**
 * Test suite for NonLinearTable.
 */
class NonLinearTableTest extends GrayscaleToASCIITableTestBase {

  override def createTable(): GrayscaleToASCIITable = {
    NonLinearTable(Seq(
      (0, '0'),   // Grayscale 0 maps to '0'
      (1, '1'),   // Grayscale 1 maps to '1'
      (3, 'A'),   // Grayscale 2-3 maps to 'A'
      (50, 'B'),  // Grayscale 4-50 maps to 'B'
      (100, 'X')  // Grayscale 50-100 (thus 50-255) maps to 'X'
    ))
  }

  test("NonLinearTableTest maps edge-case grayscale values to the correct ASCII Symbol") {
    val table = createTable()

    assert(table.getASCIISymbol(GrayscalePixel(0)) == ASCIISymbol('0'))
    assert(table.getASCIISymbol(GrayscalePixel(1)) == ASCIISymbol('1'))
    assert(table.getASCIISymbol(GrayscalePixel(2)) == ASCIISymbol('A'))
    assert(table.getASCIISymbol(GrayscalePixel(3)) == ASCIISymbol('A'))
    assert(table.getASCIISymbol(GrayscalePixel(4)) == ASCIISymbol('B'))
    assert(table.getASCIISymbol(GrayscalePixel(50)) == ASCIISymbol('B'))
    assert(table.getASCIISymbol(GrayscalePixel(51)) == ASCIISymbol('X'))
    assert(table.getASCIISymbol(GrayscalePixel(100)) == ASCIISymbol('X'))
    assert(table.getASCIISymbol(GrayscalePixel(101)) == ASCIISymbol('X'))
    assert(table.getASCIISymbol(GrayscalePixel(255)) == ASCIISymbol('X'))
  }
}
