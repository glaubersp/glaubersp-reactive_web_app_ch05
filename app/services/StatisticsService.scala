package services
import java.time._

import data.{StoredCounts, TwitterCounts}
import exceptions.{
  CountRetrievalException,
  CountStorageException,
  StatisticsServiceFailed,
  TwitterServiceException
}
import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

trait StatisticsService {

  def createUserStatistics(userName: String): Future[String]

}

class DefaultStatisticsService @Inject()(
  statisticsRepository: StatisticsRepository,
  twitterService: TwitterService
)(implicit ec: ExecutionContext)
    extends StatisticsService {

  override def createUserStatistics(userName: String): Future[String] = {

    // first group of steps: retrieving previous and current counts
    val previousCounts: Future[StoredCounts] =
      statisticsRepository.retrieveLatestCounts(userName)
    val currentCounts: Future[TwitterCounts] =
      twitterService.fetchRelationshipCounts(userName)

    val counts: Future[(StoredCounts, TwitterCounts)] = for {
      previous <- previousCounts
      current  <- currentCounts
    } yield {
      (previous, current)
    }

    // second group of steps: using the counts in order to store them and publish a message on Twitter
    val storedCounts: Future[Unit] = counts.flatMap(storeCounts(userName))
    val publishedMessage: Future[String] = counts.flatMap(publishMessage(userName))

    val result = for {
      _     <- storedCounts
      tweet <- publishedMessage
    } yield { tweet }

    result recoverWith {
      case CountStorageException(countsToStore) =>
        retryStoring(countsToStore, attemptNumber = 0)
    } recover {
      case CountStorageException(countsToStore) =>
        throw StatisticsServiceFailed(
          "We couldn't save the statistics to our database. Next time it will work!"
        )
      case CountRetrievalException(user, cause) =>
        throw StatisticsServiceFailed("We have a problem with our database. Sorry!", cause)
      case TwitterServiceException(message) =>
        throw StatisticsServiceFailed(s"We have a problem contacting Twitter: $message")
      case NonFatal(t) =>
        throw StatisticsServiceFailed("We have an unknown problem. Sorry!", t)
    }
    result
  }

  private def storeCounts(userName: String)(counts: (StoredCounts, TwitterCounts)): Future[Unit] =
    counts match {
      case (previous, current) =>
        statisticsRepository.storeCounts(
          StoredCounts(LocalDateTime.now, userName, current.followersCount, current.friendsCount)
        )
    }

  private def publishMessage(
    userName: String
  )(counts: (StoredCounts, TwitterCounts)): Future[String] =
    counts match {
      case (previous, current) =>
        val followersDifference = current.followersCount - previous.followersCount
        val friendsDifference = current.friendsCount - previous.friendsCount
        def phrasing(difference: Long) =
          if (difference >= 0) "gained" else "lost"
        val durationInDays =
          Period
            .between(previous.when.toLocalDate, LocalDateTime.now.toLocalDate)
            .getDays

        val tweet = s"@$userName in the past $durationInDays days you have " +
        s"${phrasing(followersDifference)} $followersDifference " +
        s"followers and ${phrasing(followersDifference)} " +
        s"$friendsDifference friends"

        twitterService.postTweet(tweet)
        Future.successful(tweet)
    }
  private def retryStoring(counts: StoredCounts, attemptNumber: Int)(
    implicit
    ec: ExecutionContext
  ): Future[Unit] = {
    if (attemptNumber < 3) {
      statisticsRepository.storeCounts(counts).recoverWith {
        case NonFatal(t) => retryStoring(counts, attemptNumber + 1)
      }
    } else {
      Future.failed(CountStorageException(counts))
    }
  }

}
