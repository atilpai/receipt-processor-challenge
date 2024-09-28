import akka.Done
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.settings.ServerSettings
import com.typesafe.config.ConfigFactory
import repository.{ReceiptKeyValueRepo, ReceiptRepo}
import route.HttpRouteHandler
import service.ReceiptProcessor

import java.util.logging.Logger
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

/**
 * Reference: https://doc.akka.io/docs/akka-http/current/introduction.html
 */
object Main {
  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("receipt-processor-challenge-system")
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    implicit val logger: Logger = Logger.getGlobal
    val config = ConfigFactory.load()

    // Initializations
    val repository: ReceiptRepo = new ReceiptKeyValueRepo()
    val service = new ReceiptProcessor(repository)
    val routes = {
      val routesObj = new HttpRouteHandler(service)
      routesObj.getPoints ~ routesObj.processReceipt
    }

    // Bind the route to the HTTP server
    val bindingFuture = Http()
      .newServerAt(config.getString("http.interface"), config.getInt("http.port"))
      .withSettings(ServerSettings(system))
      .bind(routes)

    bindingFuture.onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        logger info s"Server online at http://${address.getHostString}:${address.getPort}/"
        CoordinatedShutdown(system).addTask(CoordinatedShutdown.PhaseBeforeServiceUnbind, "check-shutdown-reason") { () =>
          logger info ("CoordinatedShutdown triggered with reason: " + CoordinatedShutdown(system).shutdownReason())
          Future.successful(Done)
        }

      case Failure(ex) =>
        system.terminate().onComplete(_ => sys.error(s"Failed to start, error=[$ex]"))
    }

    Await.result(system.whenTerminated, Duration.Inf)
  }
}
