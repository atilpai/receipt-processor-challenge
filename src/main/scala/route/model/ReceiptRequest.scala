package route.model

import repository.model.{Receipt, ReceiptItem}

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter
import scala.collection.mutable.ListBuffer
import scala.util.Try

case class ReceiptRequest(retailer: String,
                          purchaseDate: String,
                          purchaseTime: String,
                          total: Float,
                          items: Vector[ReceiptItem]) {

  def validate(): Either[Set[String], Receipt] = {
    val errors = ListBuffer.empty[String]
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val formattedPurchaseDate = Try(LocalDate.parse(purchaseDate, dateFormatter)).toEither match {
      case Left(error) =>
        errors.append(s"purchaseDate format must be `yyyy-MM-dd`. Error=[${error.getMessage}]")
        None
      case Right(date) => Some(date)
    }
    val formattedPurchaseTime = Try(LocalTime.parse(purchaseTime, timeFormatter)).toEither match {
      case Left(error) =>
        errors.append(s"purchaseTime format must be `HH:mm`. Error=[${error.getMessage}]")
        None
      case Right(time) => Some(time)
    }

    if (errors.isEmpty) Right(Receipt(retailer = retailer,
      purchaseDate = formattedPurchaseDate.get,
      purchaseTime = formattedPurchaseTime.get,
      total = total, items = items)) else Left(errors.toSet)
  }
}
