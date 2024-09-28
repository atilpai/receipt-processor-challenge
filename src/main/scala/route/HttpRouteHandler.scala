package route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import exception.{ReceiptNotFoundException, ReceiptRepoSaveFailException}
import route.model.{Message, ProcessReceiptResponse, ReceiptRequest}
import service.ReceiptProcessor
import service.model.Points

class HttpRouteHandler(service: ReceiptProcessor) {
  /**
   * Get receipt for id, calculate points and return
   */
  val getPoints: Route = pathPrefix("receipts" / Segment / "points") { id: String =>
    get {
      complete(try {
        Points(service.getPoints(id))
      } catch {
        case ex: ReceiptNotFoundException => (StatusCodes.NotFound, Message(ex.message))
        case ex: Exception => (StatusCodes.InternalServerError, Message(ex.getMessage))
      })
    }
  }

  /**
   * Save receipt to db
   */
  val processReceipt: Route = pathPrefix("receipts" / "process") {
    post {
      entity(as[ReceiptRequest]) { receiptReq =>
        complete(try {
          receiptReq.validate() match {
            case Left(errors) => (StatusCodes.BadRequest, Message("Receipt body validation failed.", Some(errors)))
            case Right(receipt) => ProcessReceiptResponse(service.appendReceipt(receipt))
          }
        } catch {
          case ex: ReceiptRepoSaveFailException => (StatusCodes.InternalServerError, Message(ex.message))
          case ex: Exception => (StatusCodes.InternalServerError, Message(ex.getMessage))
        })
      }
    }
  }
}
