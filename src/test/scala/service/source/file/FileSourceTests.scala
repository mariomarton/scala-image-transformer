package service.source.file

import java.nio.file.{Files, Path}
import java.awt.image.BufferedImage
import java.nio.file.StandardOpenOption.*
import java.nio.file.StandardCopyOption.REPLACE_EXISTING
import javax.imageio.ImageIO
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.BeforeAndAfterAll
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*

import constants.Constants
import domain.BufferedImageAdapter
import service.exceptions.source.{FileNotFoundException, ImageReadException, InvalidImageExtensionException, InvalidImageFileException}
import testutils.TestUtils

class FileSourceTests extends AnyFunSuite with BeforeAndAfterAll {

  val tempDir: Path = Files.createTempDirectory("fileSourceTests")

  val extensions: Set[String] = Constants.STANDARD_FILE_SOURCE_SUPPORTED_EXTENSIONS

  // Valid paths for which files will be created
  val validImageValidPath: Path = tempDir.resolve("test.jpg")
  val invalidExtensionValidPath: Path = tempDir.resolve("invalid.txt")
  val corruptedImageValidPath: Path = tempDir.resolve("corrupted.jpg")
  val nonExistentPath: Path = tempDir.resolve("nonexistent.jpg")
  val emptyFilePath: Path = tempDir.resolve("empty.jpg")

  val mockValidator: FileValidator = mock(classOf[FileValidator])

  /**
   * Simplifies setting the mockValidator to do nothing for multiple file paths.
   *
   * @param filePaths The sequence of file paths to validate.
   */
  def mockDoNothingValidation(filePaths: Seq[Path]): Unit = {
    filePaths.foreach { filePath =>
      doNothing().when(mockValidator).validate(filePath, extensions)
    }
  }

  // Setup before all tests
  override def beforeAll(): Unit = {
    TestUtils.createImage(validImageValidPath)

    Files.write(emptyFilePath, Array.emptyByteArray) // Create an empty file
    Files.writeString(invalidExtensionValidPath, "This is not an image.", CREATE, WRITE)
    Files.copy(invalidExtensionValidPath, corruptedImageValidPath, REPLACE_EXISTING)

    mockDoNothingValidation(Seq(validImageValidPath, corruptedImageValidPath, emptyFilePath))

    doThrow(new InvalidImageExtensionException("Unsupported extension")).when(mockValidator)
      .validate(invalidExtensionValidPath, extensions)

    doThrow(new FileNotFoundException("File not found")).when(mockValidator)
      .validate(nonExistentPath, extensions)
  }

  // Cleanup after all tests
  override def afterAll(): Unit = {
    TestUtils.cleanUpFiles(
      validImageValidPath,
      invalidExtensionValidPath,
      corruptedImageValidPath,
      emptyFilePath,
      tempDir
    )
  }

  // Helper function to create a fileSource for valid paths
  def createFileSource(path: Path): FileSource[BufferedImageAdapter] = new StandardFileSource(path, mockValidator)

  test("StandardFileSource loads valid image correctly as BufferedImageAdapter type") {
    val fileSource = createFileSource(validImageValidPath)
    val adapter = fileSource.getImage
    assert(adapter.isInstanceOf[BufferedImageAdapter], "StandardFileSource did not return a BufferedImageAdapter.")
  }

  test("StandardFileSource throws FileNotFoundException for non-existent file") {
    val fileSource = createFileSource(nonExistentPath)

    assertThrows[FileNotFoundException] {
      fileSource.getImage
    }
  }

  test("StandardFileSource throws InvalidImageExtensionException for unsupported extensions") {
    val fileSource = createFileSource(invalidExtensionValidPath)

    assertThrows[InvalidImageExtensionException] {
      fileSource.getImage
    }
  }

  test("StandardFileSource throws InvalidImageFileException for corrupted image files") {
    val fileSource = createFileSource(corruptedImageValidPath)

    assertThrows[InvalidImageFileException] {
      fileSource.getImage
    }
  }

  // Test that all supported extensions can be loaded
  extensions.foreach { ext =>
    test(s"StandardFileSource can load valid image with .$ext extension") {
      val validPath = tempDir.resolve(s"test.$ext")
      TestUtils.createImage(validPath, ext)

      doNothing().when(mockValidator).validate(validPath, extensions)

      val fileSource = createFileSource(validPath)
      val adapter = fileSource.getImage
      assert(adapter.isInstanceOf[BufferedImageAdapter], s"Failed to load image with .$ext extension.")

      Files.deleteIfExists(validPath)
    }
  }

  test("StandardFileSource throws InvalidImageFileException for empty image file") {
    val fileSource = createFileSource(emptyFilePath)

    assertThrows[InvalidImageFileException] {
      fileSource.getImage
    }
  }
}
