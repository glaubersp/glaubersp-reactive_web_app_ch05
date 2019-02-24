package controllers

import javax.inject._
import play.api._
import play.api.libs.ws.WSClient
import play.api.mvc._
import services._

import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject()(
  cc: ControllerComponents
)(
  implicit ec: ExecutionContext,
  ws: WSClient,
  config: Configuration
) extends AbstractController(cc) {

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def loadStatistics(accountOpt: Option[String]): Action[AnyContent] = Action.async {
    val account = accountOpt.getOrElse("TwitterEng")
    implicit val twitterCredentials: TwitterCredentials = new TwitterCredentials(config)
    val statisticsRepository = new MongoStatisticsRepository
    val twitterService = new WSTwitterService
    val service = new DefaultStatisticsService(statisticsRepository, twitterService)
    val tweet = service.createUserStatistics(account)
    tweet.map { msg =>
      Ok(msg)
    }
  }
}
