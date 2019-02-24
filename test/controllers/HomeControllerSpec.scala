package controllers

import org.scalatestplus.play._
import org.scalatestplus.play.guice._
import play.api.{Configuration, Environment}
import play.api.test.Helpers._
import play.api.test._
import services.TwitterCredentials

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

/**
  * Add your spec here.
  * You can mock out a whole application including requests, plugins etc.
  *
  * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
  */
class HomeControllerSpec extends PlaySpec with GuiceOneAppPerTest with Injecting {

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val config: Configuration = Configuration.load(Environment.simple())
  implicit val twitterCredentials: TwitterCredentials = new TwitterCredentials(config)

  "HomeController GET" should {

    "render the index page from a new instance of controller" in {
      WsTestClient.withClient { implicit ws =>
        val controller = new HomeController(stubControllerComponents())
        val home = controller.index().apply(FakeRequest(GET, "/"))

        status(home) mustBe OK
        contentType(home) mustBe Some("text/html")
        contentAsString(home) must include("Welcome to Play")
      }
    }

    "render the index page from the application" in {
      val controller = inject[HomeController]
      val home = controller.index().apply(FakeRequest(GET, "/"))

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "render the index page from the router" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include("Welcome to Play")
    }

    "compute and publish statistics for specific user" in {
      val request = FakeRequest(GET, "/loadStatistics?account=glaubersp")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("glaubersp")
    }

    "compute and publish statistics for default user" in {
      val request = FakeRequest(GET, "/loadStatistics")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/plain")
      contentAsString(home) must include("TwitterEng")
    }
  }

}
