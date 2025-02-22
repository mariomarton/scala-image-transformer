package service.transformer.grayscaletoascii

import domain.{ASCIIImage, ASCIISymbol, GrayscaleImage, GrayscalePixel}
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers.any
import testutils.{TestData, TestUtils}

/**
 * Test suite for TableBasedGrayscaleToASCIIAlgorithm.
 */
class TableBasedGrayscaleToASCIIAlgorithmTests extends AnyFunSuite with MockitoSugar {

  private def createMockTable(symbolMap: Map[GrayscalePixel, ASCIISymbol]): GrayscaleToASCIITable = {
    val mockTable = mock[GrayscaleToASCIITable]

    // For defined pixels, use the map
    symbolMap.foreach { case (pixel, symbol) =>
      when(mockTable.getASCIISymbol(pixel)).thenReturn(symbol)
    }

    // For undefined pixels, use the fallback
    when(mockTable.getASCIISymbol(any[GrayscalePixel])).thenAnswer { invocation =>
      val pixel = invocation.getArgument(0, classOf[GrayscalePixel])
      symbolMap.getOrElse(pixel, ASCIISymbol('.')) // Check the map first, fallback otherwise
    }

    mockTable
  }


  // Function for any test of the algorithm that compares expected and actual output image
  private def runConversionTest(
                                 testName: String,
                                 grayscaleImageData: Seq[Seq[Int]],
                                 expectedAsciiImage: ASCIIImage,
                                 symbolMap: Map[GrayscalePixel, ASCIISymbol]
                               ): Unit = {
    test(testName) {
      val mockTable = createMockTable(symbolMap)
      val grayscaleImage = GrayscaleImage(TestUtils.intsToGrayscalePixels(grayscaleImageData))
      val algorithm = new TableBasedGrayscaleToASCIIAlgorithm(mockTable)
      val asciiImage = algorithm.run(grayscaleImage)

      assert(asciiImage == expectedAsciiImage)
    }
  }

  // Run tests
  runConversionTest(
    "Converts a simple GrayscaleImage correctly",
    TestData.SimpleGrayscaleImage,
    TestData.SimpleGrayscaleImageASCII,
    Map(
      GrayscalePixel(0) -> ASCIISymbol('A'),
      GrayscalePixel(16) -> ASCIISymbol('Q'),
      GrayscalePixel(32) -> ASCIISymbol('G'),
      GrayscalePixel(48) -> ASCIISymbol('W'),
      GrayscalePixel(65) -> ASCIISymbol('N'),
      GrayscalePixel(68) -> ASCIISymbol('Q')
    )
  )

  runConversionTest(
    "Converts a single-pixel GrayscaleImage correctly",
    TestData.SinglePixelGrayscaleImage,
    TestData.SinglePixelGrayscaleImageASCII,
    Map(GrayscalePixel(8) -> ASCIISymbol('I'))
  )

  runConversionTest(
    "Converts a large uniform GrayscaleImage correctly",
    TestUtils.generateRandomGrayscalePixels(360, 240, seed = 12345),
    ASCIIImage(Seq.fill(240)(Seq.fill(360)(ASCIISymbol('.')))),
    symbolMap = Map.empty
  )

  runConversionTest(
    "Converts an empty GrayscaleImage correctly",
    Seq.empty,
    ASCIIImage(Seq.empty),
    symbolMap = Map.empty
  )

  test("Ensures table is invoked for every pixel") {
    val mockTable = mock[GrayscaleToASCIITable]
    when(mockTable.getASCIISymbol(any[GrayscalePixel])).thenReturn(ASCIISymbol('.'))

    val grayscaleImageData = Seq(
      Seq(0, 128),
      Seq(255, 128)
    )
    val grayscaleImage = GrayscaleImage(TestUtils.intsToGrayscalePixels(grayscaleImageData))
    val algorithm = new TableBasedGrayscaleToASCIIAlgorithm(mockTable)

    algorithm.run(grayscaleImage)

    verify(mockTable).getASCIISymbol(GrayscalePixel(0))
    verify(mockTable).getASCIISymbol(GrayscalePixel(255))
    verify(mockTable, times(2)).getASCIISymbol(GrayscalePixel(128)) // Called twice
  }
}
