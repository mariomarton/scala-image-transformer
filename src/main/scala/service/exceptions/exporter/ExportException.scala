package service.exceptions.exporter

abstract class ExportException(message: String, cause: Throwable = null) extends Exception(message, cause)
