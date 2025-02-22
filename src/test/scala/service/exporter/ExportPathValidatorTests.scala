package service.exporter

import org.scalatest.funsuite.AnyFunSuite
import service.exceptions.exporter.ExportPathValidationException

import java.nio.file.{Files, Path}

class ExportPathValidatorTests extends AnyFunSuite {

  test("DefaultExportPathValidator throws exception for non-existing directory") {
    val validator = DefaultExportPathValidator
    val invalidPath: Path = Path.of("/non/existent/directory/output.txt")

    assertThrows[ExportPathValidationException] {
      validator.validatePath(invalidPath)
    }
  }

  test("DefaultExportPathValidator passes validation for valid file path") {
    val validator = DefaultExportPathValidator
    val tempDir: Path = Files.createTempDirectory("exporterTests")
    val validPath: Path = tempDir.resolve("output.txt")

    validator.validatePath(validPath) // Should not throw any exception

    assert(Files.exists(tempDir), "Temporary directory should exist.")
    Files.deleteIfExists(tempDir)
  }

  // tests for non-writable file and directory are seemingly problematic to run on gitlab so i will not include them
}
