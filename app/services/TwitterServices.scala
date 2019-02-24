package services

import data.TwitterCounts
import exceptions.TwitterServiceException
import javax.inject.Inject
import play.api.libs.oauth.OAuthCalculator
import play.api.libs.ws.WSClient

import scala.concurrent.{ExecutionContext, Future}

trait TwitterService {

  def fetchRelationshipCounts(userName: String)(
    implicit
    ec: ExecutionContext
  ): Future[TwitterCounts]

  def postTweet(message: String): Future[Unit]

}

class WSTwitterService @Inject()(
  implicit credentials: TwitterCredentials,
  ws: WSClient,
  ec: ExecutionContext
) extends TwitterService {
  override def fetchRelationshipCounts(
    userName: String
  )(implicit ec: ExecutionContext): Future[TwitterCounts] = {

    credentials.getCredentials
      .map {
        case (consumerKey, requestToken) =>
          ws.url("https://api.twitter.com/1.1/users/show.json")
            .sign(OAuthCalculator(consumerKey, requestToken))
            .addQueryStringParameters("screen_name" -> userName)
            .get()
            .map { response =>
              if (response.status == 200) {
                TwitterCounts(
                  (response.json \ "followers_count").as[Long],
                  (response.json \ "friends_count").as[Long]
                )
              } else {
                throw TwitterServiceException(
                  s"Could not retrieve counts for Twitter user $userName"
                )
              }
            }
      }
      .getOrElse {
        Future.failed(
          TwitterServiceException("You did not correctly configure the Twitter credentials")
        )
      }

  }

  override def postTweet(message: String): Future[Unit] = Future.successful {
    println("TWITTER: " + message)
  }
}
