package repository

import exception.{ReceiptNotFoundException, ReceiptRepoSaveFailException}
import repository.model.Receipt

import java.time.{LocalDateTime, ZoneOffset}
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.{Level, Logger}
import scala.jdk.CollectionConverters._
import scala.util.Try

class ReceiptKeyValueRepo(implicit logger: Logger) extends ReceiptRepo {
  override val db: scala.collection.concurrent.Map[String, Receipt] = new ConcurrentHashMap[String, Receipt]().asScala

  /**
   * Adds receipt to repo
   * @param receipt
   * @return
   */
  def addReceipt(receipt: Receipt): String = {
    // Trying to be as unique and random as possible
    val id = s"${receipt.retailer}_${LocalDateTime.now().atOffset(ZoneOffset.UTC).toEpochSecond}_${UUID.randomUUID()}"
    Try(db.addOne((id, receipt))).toEither match {
      case Left(error) => logger severe s"Error=[${error.getMessage}] occurred while saving receipt"
      throw ReceiptRepoSaveFailException("Error occurred while saving receipt, please try again.")
      case Right(_) => id
    }
  }

  /**
   * Gets receipt from repo
   * @param id
   * @return
   */
  def getReceipt(id: String): Receipt = {
    db.get(id) match {
      case Some(receipt) => receipt
      case None =>
        logger log(Level.WARNING, s"There is no receipt found for id=[$id]")
        throw ReceiptNotFoundException(s"There is no receipt found for id=[$id]")
    }
  }
}
