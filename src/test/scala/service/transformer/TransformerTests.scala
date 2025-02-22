package service.transformer

import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar.mock
import org.mockito.Mockito.*
import org.mockito.ArgumentMatchers.*

import domain.*
import service.transformer.*
import testutils.{TestRGBPixel, TestRGBImage}

class TransformerTests extends AnyFunSuite {

  // Mocked transformers for TestImage and TestRegularImage
  val testImageTransformerMock: ToImageTransformer[TestRGBImage] {type Input = TestRGBImage}
  = mock[ToImageTransformer[TestRGBImage] {
    type Input = TestRGBImage
  }]

  val regularImageTransformerMock: ToRegularImageTransformer[TestRGBImage] {type Input = TestRGBImage}
  = mock[ToRegularImageTransformer[TestRGBImage] {
    type Input = TestRGBImage
  }]

  // Define some test points
  val testPoints: List[List[TestRGBPixel]] = List(
    List(TestRGBPixel(4, 8, 15), TestRGBPixel(16, 23, 42)),
    List(TestRGBPixel(1, 0, 255), TestRGBPixel(19, 7, 99))
  )

  val testImage: TestRGBImage = TestRGBImage(testPoints)

  when(testImageTransformerMock.transform(testImage)).thenReturn(testImage)
  when(regularImageTransformerMock.transform(testImage)).thenReturn(testImage)

  test("ToImageTransformer should return the same test image") {
    val transformedImage = testImageTransformerMock.transform(testImage)
    assert(transformedImage == testImage, "The transformed TestRGBImage does not match the expected image.")
  }

  test("ToRegularImageTransformer should return the same test image") {
    val transformedImage = regularImageTransformerMock.transform(testImage)
    assert(transformedImage == testImage, "The transformed TestRGBImage does not match the expected image.")
  }
}
