package data

import java.time.{Instant, LocalDateTime, ZoneOffset}

import reactivemongo.bson.{BSONDateTime, BSONDocument, BSONDocumentReader, BSONDocumentWriter}

case class StoredCounts(
  when: LocalDateTime,
  userName: String,
  followersCount: Long,
  friendsCount: Long
)

object StoredCounts {

  implicit object UserCountsReader
      extends BSONDocumentReader[StoredCounts]
      with BSONDocumentWriter[StoredCounts] {
    override def read(bson: BSONDocument): StoredCounts = {
      val when =
        bson
          .getAs[BSONDateTime]("when")
          .map(t => LocalDateTime.ofInstant(Instant.ofEpochMilli(t.value), ZoneOffset.UTC))
          .get
      val userName = bson.getAs[String]("userName").get
      val followersCount = bson.getAs[Long]("followersCount").get
      val friendsCount = bson.getAs[Long]("friendsCount").get
      StoredCounts(when, userName, followersCount, friendsCount)
    }

    override def write(t: StoredCounts): BSONDocument =
      BSONDocument(
        "when"           -> BSONDateTime(t.when.toInstant(ZoneOffset.UTC).toEpochMilli),
        "userName"       -> t.userName,
        "followersCount" -> t.followersCount,
        "friendsCount"   -> t.friendsCount
      )
  }

}
