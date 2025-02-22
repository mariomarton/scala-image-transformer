package service.transformer.grayscaletoascii

import constants.Constants as const

/**
 * Utility object providing default tables and algorithms.
 */
object GrayscaleToASCIIUtilities {

  def defaultLinearTable(): LinearTable = {
    LinearTable(const.DEFAULT_CHARACTER_SET)
  }

  def defaultNonLinearTable(): NonLinearTable = {
    NonLinearTable(const.DEFAULT_RANGE_SET)
  }

  def getLinearTable(symbols: String): LinearTable = {
    val charSequence: Seq[Char] = symbols.toSeq
    LinearTable(charSequence)
  }
  
  def defaultLinearAlgorithm(): GrayscaleToASCIIAlgorithm = {
    new TableBasedGrayscaleToASCIIAlgorithm(defaultLinearTable())
  }

  def defaultNonLinearAlgorithm(): GrayscaleToASCIIAlgorithm = {
    new TableBasedGrayscaleToASCIIAlgorithm(defaultNonLinearTable())
  }
}