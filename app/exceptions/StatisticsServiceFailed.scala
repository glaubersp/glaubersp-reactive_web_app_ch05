package exceptions

class StatisticsServiceFailed(cause: Throwable)
  extends RuntimeException(cause) {
  def this(message: String) = this(new RuntimeException(message))
  def this(message: String, cause: Throwable) =
    this(new RuntimeException(message, cause))
}

object StatisticsServiceFailed {
  def apply(message: String): StatisticsServiceFailed =
    new StatisticsServiceFailed(message)
  def apply(message: String, cause: Throwable): StatisticsServiceFailed =
    new StatisticsServiceFailed(message, cause)
}
