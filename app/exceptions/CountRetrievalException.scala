package exceptions

case class CountRetrievalException(userName: String, cause: Throwable)
  extends RuntimeException("Could not read counts for " + userName, cause)
