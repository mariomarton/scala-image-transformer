package controller.exceptions

abstract class ControllerException(message: String, cause: Throwable = null) extends Exception(message, cause)