package repository

import repository.model.Receipt

trait ReceiptRepo {

  val db: Any

  def addReceipt(receipt: Receipt): String

  def getReceipt(id: String): Receipt
}
