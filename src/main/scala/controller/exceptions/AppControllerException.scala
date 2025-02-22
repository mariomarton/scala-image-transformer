package controller.exceptions

class AppControllerException(message: String, cause: Throwable = null) extends ControllerException(message, cause)
