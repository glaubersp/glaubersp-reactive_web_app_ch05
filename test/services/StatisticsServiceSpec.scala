//package services
//
//import org.specs2.concurrent.ExecutionEnv
//import org.specs2.specification.core.Fragment
//import play.api.libs.ws.WSClient
//import play.api.test.{PlaySpecification, WithServer}
//
//import scala.concurrent.Future
//import scala.concurrent.duration._
//
//class StatisticsServiceSpec() extends PlaySpecification {
//
//  def is(implicit ee: ExecutionEnv): Fragment = {
//    "The StatisticsService" should {
//
//      "compute and publish statistics" in new WithServer() {
//        implicit val twitterCredentials: TwitterCredentials =
//          app.injector.instanceOf[TwitterCredentials]
//        implicit val ws: WSClient = app.injector.instanceOf[WSClient]
//        val repository =
//          new MongoStatisticsRepository()
//        val wsTwitterService = new WSTwitterService
//        val service = new DefaultStatisticsService(repository, wsTwitterService)
//
//        val f: Future[String] = service.createUserStatistics("glaubersp")
//
//        f must beAnInstanceOf[String].await(retries = 0, timeout = 10.seconds)
//      }
//    }
//  }
//}
