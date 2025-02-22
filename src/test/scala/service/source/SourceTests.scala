package service.source

import domain.ImageBase
import org.scalatest.funsuite.AnyFunSuite

class SourceTests extends AnyFunSuite {

  // Dummy ImageBase implementation for testing
  case class DummyImage() extends ImageBase

  // Dummy source class for testing the Source trait
  class DummySource extends Source[DummyImage] {
    override def getImage: DummyImage = DummyImage()
  }

  test("Source trait returns correct image type") {
    val source = new DummySource
    assert(source.getImage.isInstanceOf[DummyImage], "Source did not return an instance of the correct image type.")
  }
}
