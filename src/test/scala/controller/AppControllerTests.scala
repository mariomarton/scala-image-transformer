package controller

import controller.exceptions.AppControllerException
import domain.{ASCIIImage, BufferedImageAdapter, GrayscaleImage, GrayscalePixel, ASCIISymbol, ImageBase, Point, RGBImage, RegularCustomImage}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import service.exporter.{ConsoleExporter, Exporter, FileExporter}
import service.source.Source
import service.transformer.filter.{Filter, GrayscaleFilter, InvertFilter}
import service.transformer.grayscaletoascii.{GrayscaleToASCIIAlgorithm, GrayscaleToASCIITable, TableBasedGrayscaleToASCIIAlgorithm}
import testutils.TestUtils

import scala.collection.immutable.Seq

class AppControllerTests extends AnyFunSuite with BeforeAndAfterEach {

  private var mockSource: Source[ImageBase] = _
  private var mockExporter: Exporter = _
  private var mockFilters: List[Filter[_]] = _
  private var mockTable: GrayscaleToASCIITable = _

  override def beforeEach(): Unit = {
    mockSource = mock(classOf[Source[ImageBase]])
    mockExporter = mock(classOf[Exporter])
    mockFilters = List(mock(classOf[GrayscaleFilter]))
    mockTable = mock(classOf[GrayscaleToASCIITable])
  }

  private def createAppConfig(
                               source: Source[ImageBase] = mockSource,
                               filters: List[Filter[_]] = mockFilters,
                               table: GrayscaleToASCIITable = mockTable,
                               exporters: List[Exporter] = List(mockExporter)
                             ): AppConfig = {
    AppConfig(source = source, filters = filters, table = table, exporters = exporters)
  }

  /**
   * Creates GrayscaleImage for a sequence of ints and mocks behaviour of the related table.
   */
  private def createGrayscaleImageAndMockConversion(pixels: Seq[Seq[Int]], symbols: Seq[Seq[Char]]): GrayscaleImage = {
    val grayscaleImage = TestUtils.createGrayscaleImage(pixels)
    for ((row, y) <- pixels.zipWithIndex; (value, x) <- row.zipWithIndex) {
      when(mockTable.getASCIISymbol(GrayscalePixel(value))).thenReturn(ASCIISymbol(symbols(y)(x)))
    }
    grayscaleImage
  }

  private def createASCIIImage(symbols: Seq[Seq[Char]]): ASCIIImage = {
    ASCIIImage(symbols.map(_.map(ASCIISymbol.apply)))
  }

  private val grayscaleImage: GrayscaleImage = TestUtils.createGrayscaleImage(Seq(Seq(10, 20), Seq(30, 40)))

  /**
   * Runs two filters on a grayscaleImage (defined above),
   * returns (list of filters, image after filter 1, image after filter 2).
   */
  private def runFilters(): (List[GrayscaleFilter], GrayscaleImage, GrayscaleImage) = {
    val filters = List(mock(classOf[GrayscaleFilter]), mock(classOf[GrayscaleFilter]))
    val filteredImage1: GrayscaleImage = TestUtils.createGrayscaleImage(Seq(Seq(15, 25), Seq(35, 45)))
    val filteredImage2: GrayscaleImage = TestUtils.createGrayscaleImage(Seq(Seq(20, 30), Seq(40, 50)))

    when(mockSource.getImage).thenReturn(grayscaleImage)
    when(filters.head.transform(grayscaleImage)).thenReturn(filteredImage1)
    when(filters(1).transform(filteredImage1)).thenReturn(filteredImage2)

    new AppController().run(createAppConfig(filters = filters))

    (filters, filteredImage1, filteredImage2)
  }

  test("AppController runs successfully with valid configuration") {
    val grayscaleImage = createGrayscaleImageAndMockConversion(
      Seq(Seq(10, 20), Seq(30, 40)),
      Seq(Seq('@', '#'), Seq('$', '%'))
    )
    val asciiImage = createASCIIImage(Seq(Seq('@', '#'), Seq('$', '%')))

    when(mockSource.getImage).thenReturn(grayscaleImage)
    when(mockFilters.head.asInstanceOf[GrayscaleFilter].transform(grayscaleImage)).thenReturn(grayscaleImage)

    new AppController().run(createAppConfig())

    verify(mockSource).getImage
    verify(mockFilters.head.asInstanceOf[GrayscaleFilter]).transform(grayscaleImage)
    verify(mockExporter).exportASCII(asciiImage)
  }

  test("AppController throws exception for unsupported image type") {
    when(mockSource.getImage).thenReturn(mock(classOf[RegularCustomImage[Point]]))

    assertThrows[AppControllerException] {
      new AppController().run(createAppConfig(exporters = List.empty))
    }
  }

  test("AppController: all filters are run") {
    val (filters, filteredImage1, _) = runFilters()

    verify(filters.head).transform(grayscaleImage)
    verify(filters(1)).transform(filteredImage1)
  }

  test("AppController: filters are run in the right order") {
    val (filters, filteredImage1, _) = runFilters()

    val order = inOrder(filters: _*)
    order.verify(filters.head).transform(grayscaleImage)
    order.verify(filters(1)).transform(filteredImage1)
  }

  test("AppController supports multiple exporters") {
    val grayscaleImage = createGrayscaleImageAndMockConversion(
      Seq(Seq(10, 20), Seq(30, 40)),
      Seq(Seq('@', '#'), Seq('$', '%'))
    )
    val asciiImage = createASCIIImage(Seq(Seq('@', '#'), Seq('$', '%')))

    val exporter1 = mock(classOf[Exporter])
    val exporter2 = mock(classOf[Exporter])

    when(mockSource.getImage).thenReturn(grayscaleImage)
    when(mockFilters.head.asInstanceOf[GrayscaleFilter].transform(grayscaleImage)).thenReturn(grayscaleImage)

    new AppController().run(createAppConfig(exporters = List(exporter1, exporter2)))

    verify(exporter1).exportASCII(asciiImage)
    verify(exporter2).exportASCII(asciiImage)
  }

  test("AppController handles empty filters correctly") {
    val grayscaleImage = createGrayscaleImageAndMockConversion(
      Seq(Seq(10, 20), Seq(30, 40)),
      Seq(Seq('@', '#'), Seq('$', '%'))
    )
    val mockExporter = mock(classOf[Exporter])

    when(mockSource.getImage).thenReturn(grayscaleImage)

    new AppController().run(createAppConfig(filters = List.empty, exporters = List(mockExporter)))

    verify(mockSource).getImage
    verify(mockExporter).exportASCII(createASCIIImage(Seq(Seq('@', '#'), Seq('$', '%'))))
  }

}
