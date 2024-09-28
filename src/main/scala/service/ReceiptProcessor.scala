package service

import repository.ReceiptRepo
import repository.model.Receipt

import java.time.LocalTime

/**
 * Helps service requests that deal with the ReceiptRepo
 * @param receiptKeyValueRepo
 */
class ReceiptProcessor(receiptKeyValueRepo: ReceiptRepo) {

  /**
   * Appends new receipts to the db
   * @param receipt
   * @return
   */
  def appendReceipt(receipt: Receipt): String = {
    receiptKeyValueRepo.addReceipt(receipt)
  }

  /**
   * Gets the points for a given receipt id
   * @param id
   * @return
   */
  def getPoints(id: String): Int = {
    calculatePoints(retrieveReceipt(id))
  }

  /**
   * Retrieves Receipt from repo based on id
   * @param id
   * @return
   */
  private def retrieveReceipt(id: String): Receipt = {
    receiptKeyValueRepo.getReceipt(id)
  }

  /**
   * Calculates points gained on a receipt based on rules defined in comments below
   * @param receipt
   * @return
   */
  private def calculatePoints(receipt: Receipt): Int = {
    // One point for every alphanumeric character in the retailer name
    receipt.retailer.count(_.isLetterOrDigit) +
      // 50 points if the total is a round dollar amount with no cents
      {
        if (receipt.total == receipt.total.toLong) 50 else 0
      } +
      // 25 points if the total is a multiple of 0.25
      {
        if (receipt.total % 0.25 == 0) 25 else 0
      } +
      // 5 points for every two items on the receipt
      ((receipt.items.size / 2) * 5) +
      // If the trimmed length of the item description is a multiple of 3,
      // multiply the price by 0.2 and round up to the nearest integer.
      // The result is the number of points earned
      receipt.items.foldLeft(0)((pts, item) => {
        if (item.shortDescription.trim.length % 3 == 0) math.ceil(item.price * 0.2).toInt + pts
        else pts
      }) +
      // 6 points if the day in the purchase date is odd
      {
        if (receipt.purchaseDate.getDayOfMonth % 2 != 0) 6 else 0
      } +
      // 10 points if the time of purchase is after 2:00pm and before 4:00pm
      {
        if (receipt.purchaseTime.isAfter(LocalTime.of(14, 0)) && receipt.purchaseTime.isBefore(LocalTime.of(16, 0))) 10 else 0
      }
  }
}