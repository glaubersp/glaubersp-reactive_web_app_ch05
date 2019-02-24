package exceptions
import data.StoredCounts

case class CountStorageException(counts: StoredCounts) extends RuntimeException
