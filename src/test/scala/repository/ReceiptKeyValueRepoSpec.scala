package repository

import exception.ReceiptNotFoundException
import fixture.Fixtures
import org.scalatest.GivenWhenThen
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers

import java.util.logging.Logger

class ReceiptKeyValueRepoSpec extends AnyFeatureSpec with GivenWhenThen with Matchers with Fixtures {
  implicit val logger: Logger = Logger.getGlobal
  val receiptKeyValueRepo = new ReceiptKeyValueRepo()

  Feature("Receipt CRUD") {
    Scenario("addReceipt passes") {
      Given("receipt")
      When("addReceipt is called")
      val id = receiptKeyValueRepo.addReceipt(receiptMM)

      Then("id should be a non empty String")
      assert(id.nonEmpty)
    }

    Scenario("getReceipt passes") {
      Given("id")
      val id = receiptKeyValueRepo.addReceipt(receiptMM)
      When("getReceipt is called")
      val receiptRes = receiptKeyValueRepo.getReceipt(id)
      Then("receipt matches")
      receiptRes shouldBe receiptMM
    }

    Scenario("getReceipt fails") {
      Given("non existent id")
      val id = "badId"
      When("getReceipt is called")
      assertThrows[ReceiptNotFoundException] {
        receiptKeyValueRepo.getReceipt(id)
      }
      Then("assert for thrown exception passes")
    }
  }
}
