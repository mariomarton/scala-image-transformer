package service.source.file

import java.nio.file.{Files, Path}

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite

import service.exceptions.source.*

class FileValidatorTests extends AnyFunSuite with BeforeAndAfterAll {

  val tempDir: Path = Files.createTempDirectory("fileValidatorTests")
  val validator: FileValidator = new StandardFileValidator // Instantiate the new validator

  // Paths for files that will be created
  val validPaths: Map[String, Path] = Map(
    "validFilePathValidExtension" -> tempDir.resolve("test.jpg"),
    "validFilePathValidSecondaryExtension" -> tempDir.resolve("test.gif"),
    "validFilePathInvalidExtension" -> tempDir.resolve("test.txt"),
    "validDoubleExtensionPath" -> tempDir.resolve("test.txt.jpg"),
    "invalidDoubleExtensionPath" -> tempDir.resolve("test.jpg.txt"),
    "noExtensionPath" -> tempDir.resolve("noextension")
  )

  // Paths for files that will NOT be created
  val invalidPaths: Map[String, Path] = Map(
    "invalidPathValidExtension" -> tempDir.resolve("invalid_path.jpg"),
    "invalidPathInvalidExtension" -> tempDir.resolve("invalid_path.txt")
  )

  override def beforeAll(): Unit = {
    validPaths.values.foreach(path => Files.createFile(path))
  }

  override def afterAll(): Unit = {
    // Delete all files
    validPaths.values.foreach(Files.deleteIfExists)

    // Finally, delete the temporary directory
    Files.deleteIfExists(tempDir)
  }

  test("StandardFileValidator successfully validates valid file path and extension") {
    validator.validate(validPaths("validFilePathValidExtension"), Set("jpg"))
  }

  test("StandardFileValidator successfully validates valid file path and extension (multiple allowed extensions)") {
    validator.validate(validPaths("validFilePathValidExtension"), Set("jpg", "gif"))
    validator.validate(validPaths("validFilePathValidSecondaryExtension"), Set("jpg", "gif"))
  }

  test("StandardFileValidator throws FileNotFoundException for non-existent file") {
    assertThrows[FileNotFoundException] {
      validator.validate(invalidPaths("invalidPathInvalidExtension"), Set("jpg"))
    }
  }

  test("StandardFileValidator throws InvalidImageExtensionException for unsupported extensions") {
    assertThrows[InvalidImageExtensionException] {
      validator.validate(validPaths("validFilePathInvalidExtension"), Set("jpg"))
    }
  }

  test("StandardFileValidator throws FileNotFoundException for invalid path with valid extension") {
    assertThrows[FileNotFoundException] {
      validator.validate(invalidPaths("invalidPathValidExtension"), Set("jpg"))
    }
  }

  test("StandardFileValidator throws InvalidImageExtensionException for valid path but no extension") {
    assertThrows[InvalidImageExtensionException] {
      validator.validate(validPaths("noExtensionPath"), Set("jpg"))
    }
  }

  test("StandardFileValidator successfully validates valid path with no extension if empty extension is allowed") {
    validator.validate(validPaths("noExtensionPath"), Set("jpg", ""))
  }

  test("StandardFileValidator successfully validates file with multiple extensions (.txt.jpg)") {
    validator.validate(validPaths("validDoubleExtensionPath"), Set("jpg"))
  }

  test("StandardFileValidator throws InvalidImageExtensionException for invalid multiple extensions (.jpg.txt)") {
    assertThrows[InvalidImageExtensionException] {
      validator.validate(validPaths("invalidDoubleExtensionPath"), Set("jpg"))
    }
  }

  test("StandardFileValidator successfully validates empty extension for valid path with no extension") {
    validator.validate(validPaths("noExtensionPath"), Set(""))
  }
}
