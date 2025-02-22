package service.exporter

import org.scalatest.funsuite.AnyFunSuite
import org.mockito.Mockito._
import org.mockito.ArgumentMatchers._

import java.nio.file.{Files, Path}
import domain.{ASCIIImage, ASCIISymbol}
import service.exceptions.exporter.{ExportPathValidationException, FileExportException, ConsoleExportException}

class ExporterTests extends AnyFunSuite {

  def createSampleASCIIImage(): ASCIIImage = {
    val points = List(
      List(ASCIISymbol('@'), ASCIISymbol('#')),
      List(ASCIISymbol('*'), ASCIISymbol('.'))
    )
    ASCIIImage(points)
  }

  // Tests for FileExporter
  test("FileExporter successfully writes ASCII image to file") {
    val mockValidator = mock(classOf[ExportPathValidator])
    val tempDir: Path = Files.createTempDirectory("exporterTests")
    val outputPath: Path = tempDir.resolve("output.txt")
    val fileExporter = new FileExporter(outputPath, mockValidator)

    val asciiImage = createSampleASCIIImage()

    // Validator mock does not throw any exception
    fileExporter.exportASCII(asciiImage)

    val writtenContent = Files.readAllLines(outputPath).toArray.mkString("\n")
    val expectedContent = "@#\n*."

    assert(writtenContent == expectedContent, "FileExporter did not write the expected ASCII content.")
    Files.deleteIfExists(outputPath)
    Files.deleteIfExists(tempDir)
  }

  test("FileExporter throws ExportPathValidationException when validator fails") {
    val mockValidator = mock(classOf[ExportPathValidator])
    val invalidPath: Path = Path.of("/invalid/path/output.txt")
    val fileExporter = new FileExporter(invalidPath, mockValidator)

    val asciiImage = createSampleASCIIImage()

    when(mockValidator.validatePath(invalidPath)).thenThrow(new ExportPathValidationException("Invalid path"))

    assertThrows[ExportPathValidationException] {
      fileExporter.exportASCII(asciiImage)
    }
  }
  
  // Tests for ConsoleExporter
  test("ConsoleExporter throws ImageControllerException for unexpected errors") {
    val consoleExporter = new ConsoleExporter()
    val mockImage = mock(classOf[ASCIIImage])
    when(mockImage.points).thenThrow(new RuntimeException("Simulated error"))

    val exception = intercept[ConsoleExportException] {
      consoleExporter.exportASCII(mockImage)
    }

    assert(exception.getMessage.contains("Failed to print ASCII image to the console"))
  }
}
