package fixture

import route.model.ReceiptRequest
import io.circe.generic.auto._
import io.circe.parser._
import repository.model.Receipt

trait Fixtures {

  val jsonEntityMM: String =
    """
      |{
      |  "retailer": "M&M Corner Market",
      |  "purchaseDate": "2022-03-20",
      |  "purchaseTime": "14:33",
      |  "items": [
      |    {
      |      "shortDescription": "Gatorade",
      |      "price": "2.25"
      |    },{
      |      "shortDescription": "Gatorade",
      |      "price": "2.25"
      |    },{
      |      "shortDescription": "Gatorade",
      |      "price": "2.25"
      |    },{
      |      "shortDescription": "Gatorade",
      |      "price": "2.25"
      |    }
      |  ],
      |  "total": "9.00"
      |}
      |""".stripMargin

  val jsonEntityTarget: String =
    """
      |{
      |  "retailer": "Target",
      |  "purchaseDate": "2022-01-01",
      |  "purchaseTime": "13:01",
      |  "items": [
      |    {
      |      "shortDescription": "Mountain Dew 12PK",
      |      "price": "6.49"
      |    },{
      |      "shortDescription": "Emils Cheese Pizza",
      |      "price": "12.25"
      |    },{
      |      "shortDescription": "Knorr Creamy Chicken",
      |      "price": "1.26"
      |    },{
      |      "shortDescription": "Doritos Nacho Cheese",
      |      "price": "3.35"
      |    },{
      |      "shortDescription": "   Klarbrunn 12-PK 12 FL OZ  ",
      |      "price": "12.00"
      |    }
      |  ],
      |  "total": "35.35"
      |}
      |""".stripMargin

  val receiptRequestMM: ReceiptRequest = decode[ReceiptRequest](jsonEntityMM).toOption.get
  val receiptMM: Receipt = receiptRequestMM.validate().toOption.get
  val receiptRequestTarget: ReceiptRequest = decode[ReceiptRequest](jsonEntityTarget).toOption.get
  val receiptTarget: Receipt = receiptRequestTarget.validate().toOption.get
}
