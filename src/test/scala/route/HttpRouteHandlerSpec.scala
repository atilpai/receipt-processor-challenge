package route

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import fixture.Fixtures
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import service.ReceiptProcessor
import io.circe.generic.auto._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import exception.{ReceiptNotFoundException, ReceiptRepoSaveFailException}
import route.model.Message
import service.model.Points

class HttpRouteHandlerSpec extends AnyFeatureSpec with MockFactory with ScalatestRouteTest with Fixtures with GivenWhenThen with Matchers {

  val service: ReceiptProcessor = mock[ReceiptProcessor]
  val httpRoutes = new HttpRouteHandler(service)

  Feature("Get Points route") {

    Scenario("Successful retrieval of points") {
      Given("a receipt ID")
      val receiptId = "12345"
      val expectedPoints = 100

      When("the getPoints route is called")
      (service.getPoints _).expects(receiptId).returning(expectedPoints)

      Get(s"/receipts/$receiptId/points") ~> httpRoutes.getPoints ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Points].points shouldEqual expectedPoints
      }
    }

    Scenario("Receipt not found exception") {
      Given("a receipt ID that does not exist")
      val receiptId = "unknown"

      When("the getPoints route is called")
      (service.getPoints _).expects(receiptId).throws(ReceiptNotFoundException("Receipt not found"))

      Get(s"/receipts/$receiptId/points") ~> httpRoutes.getPoints ~> check {
        status shouldEqual StatusCodes.NotFound
        responseAs[Message] shouldEqual Message("Receipt not found")
      }
    }

    Scenario("General exception during retrieval") {
      Given("a receipt ID")
      val receiptId = "12345"

      When("the getPoints route is called")
      (service.getPoints _).expects(receiptId).throws(new Exception("An error occurred"))

      Get(s"/receipts/$receiptId/points") ~> httpRoutes.getPoints ~> check {
        status shouldEqual StatusCodes.InternalServerError
        responseAs[Message] shouldEqual Message("An error occurred")
      }
    }
  }

  Feature("Process Receipt route") {

    Scenario("Successful processing of receipt") {
      Given("a valid receipt request")
      When("the processReceipt route is called")
      (service.appendReceipt _).expects(receiptTarget).returns("id")

      Post("/receipts/process", receiptRequestTarget) ~> httpRoutes.processReceipt ~> check {
        status shouldEqual StatusCodes.OK
      }
    }

    Scenario("Receipt body validation failed") {
      Given("an invalid receipt request")
      val invalidReceiptReq = receiptRequestTarget.copy(purchaseDate = "23-01-2024")

      When("the processReceipt route is called")
      Post("/receipts/process", invalidReceiptReq) ~> httpRoutes.processReceipt ~> check {
        status shouldEqual StatusCodes.BadRequest
        assert(responseAs[Message].errors.nonEmpty)
      }
    }

    Scenario("General exception during receipt processing") {
      Given("a valid receipt request")
      When("the processReceipt route is called")
      (service.appendReceipt _).expects(receiptTarget).throws(ReceiptRepoSaveFailException("Failed to save receipt"))

      Post("/receipts/process", receiptRequestTarget) ~> httpRoutes.processReceipt ~> check {
        status shouldEqual StatusCodes.InternalServerError
        assert(responseAs[Message].message.nonEmpty)
      }
    }
  }
}
