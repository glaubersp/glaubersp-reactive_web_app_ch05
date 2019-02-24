package exceptions

case class TwitterServiceException(message: String)
  extends RuntimeException(message)
