package services
import java.time._

import data.StoredCounts
import exceptions.{CountRetrievalException, CountStorageException}
import javax.inject.Inject
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api._
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.control.NonFatal

trait StatisticsRepository {

  def storeCounts(counts: StoredCounts)(
    implicit
    ec: ExecutionContext
  ): Future[Unit]

  def retrieveLatestCounts(userName: String)(
    implicit
    ec: ExecutionContext
  ): Future[StoredCounts]

}

class MongoStatisticsRepository @Inject()(
  implicit
  ec: ExecutionContext
) extends StatisticsRepository {

  private val Database = "twitterService"
  private val StatisticsCollection = "UserStatistics"
  private val driver: MongoDriver = new MongoDriver
  private var connection: MongoConnection = driver.connection(List("localhost"))
  private var db: DefaultDB =
    Await.result(connection.database(Database), 10.second)
  private var collection: BSONCollection =
    db.collection[BSONCollection](StatisticsCollection)

  override def storeCounts(counts: StoredCounts)(
    implicit
    ec: ExecutionContext
  ): Future[Unit] = {
    collection.insert.one(counts).map { lastError =>
      if (!lastError.ok) {
        throw CountStorageException(counts)
      }
    }
  }

  override def retrieveLatestCounts(userName: String)(
    implicit
    ec: ExecutionContext
  ): Future[StoredCounts] = {
    val query = BSONDocument("userName" -> userName)
    val order = BSONDocument("_id"      -> -1)
    collection
      .find(query, projection = Option.empty)
      .sort(order)
      .one[StoredCounts]
      .map { counts =>
        counts getOrElse StoredCounts(LocalDateTime.now, userName, 0, 0)
      }
  } recover {
    case NonFatal(t) =>
      throw CountRetrievalException(userName, t)
  }

}
