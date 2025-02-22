package constants

object Constants {
  // Constants used through-out the app

  // Pixel limits for random image generation
  val MIN_RANDOM_IMAGE_WIDTH: Int = 10
  val MAX_RANDOM_IMAGE_WIDTH: Int = 100
  val MIN_RANDOM_IMAGE_HEIGHT: Int = 10
  val MAX_RANDOM_IMAGE_HEIGHT: Int = 100

  // Character set for default linear table
  val DEFAULT_CHARACTER_SET: Seq[Char] = Seq(' ', '.', ':', '-', '=', '+', '*', '#', '%', '@')

  // Ranges for default non-linear table
  val DEFAULT_RANGE_SET: Seq[(Int, Char)] = Seq(
    42 -> ' ', // Grayscale 0-50 maps to ' '
    66 -> '.',
    99 -> ',',
    150 -> ':',
    200 -> '+',
    230 -> '#',
    255 -> 'X'
  )
  
  // Max value of a grayscale pixel
  val WHITE = 255

  val STANDARD_FILE_SOURCE_SUPPORTED_EXTENSIONS: Set[String] = Set("jpg", "jpeg", "png", "bmp")
}
