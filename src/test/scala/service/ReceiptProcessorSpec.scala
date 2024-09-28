package service

import exception.{ReceiptNotFoundException, ReceiptRepoSaveFailException}
import fixture.Fixtures
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import repository.{ReceiptKeyValueRepo, ReceiptRepo}
import org.scalamock.scalatest.MockFactory

class ReceiptProcessorSpec extends AnyFeatureSpec with MockFactory with GivenWhenThen with Matchers with Fixtures {
  val receiptKeyValueRepo: ReceiptRepo = mock[ReceiptKeyValueRepo]
  val receiptProcessor: ReceiptProcessor = new ReceiptProcessor(receiptKeyValueRepo)

  Feature("Receipt actions") {
    Scenario("appendReceipt passes") {
      Given("receipt")
      When("appendReceipt is called")
      receiptKeyValueRepo.addReceipt _ expects receiptMM returns "id"

      val id = receiptProcessor.appendReceipt(receiptMM)
      Then("id is returned")
      assert(id.nonEmpty)
    }

    Scenario("appendReceipt fails") {
      Given("receipt")
      When("appendReceipt is called")
      receiptKeyValueRepo.addReceipt _ expects receiptMM throws ReceiptRepoSaveFailException("ERROR")

      assertThrows[ReceiptRepoSaveFailException] {
        receiptProcessor.appendReceipt(receiptMM)
      }
      Then("error is caught")
    }

    Scenario("getPoints returns correct points") {
      Given("id")
      val idMM = "idMM"
      val idTarget = "idTarget"
      When("getPoints is called")
      receiptKeyValueRepo.getReceipt _ expects idMM returns receiptMM
      receiptKeyValueRepo.getReceipt _ expects idTarget returns receiptTarget

      val pointsMM = receiptProcessor.getPoints(idMM)
      val pointsTarget = receiptProcessor.getPoints(idTarget)
      Then("points are correct")
      pointsMM shouldBe 109
      pointsTarget shouldBe 28
    }

    Scenario("getPoints failed") {
      Given("bad id")
      val id = "badId"

      When("getPoints is called")
      receiptKeyValueRepo.getReceipt _ expects id throws ReceiptNotFoundException("ERROR")

      assertThrows[ReceiptNotFoundException] {
        receiptProcessor.getPoints(id)
      }
      Then("error is caught")
    }
  }
}
