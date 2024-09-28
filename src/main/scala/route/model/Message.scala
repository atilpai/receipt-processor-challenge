package route.model

case class Message(message: String, errors: Option[Set[String]] = None)
