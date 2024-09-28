package repository.model

import java.time.{LocalDate, LocalTime}

case class Receipt(retailer: String,
                   purchaseDate: LocalDate,
                   purchaseTime: LocalTime,
                   total: Float,
                   items: Vector[ReceiptItem])

case class ReceiptItem(shortDescription: String,
                       price: Float)
