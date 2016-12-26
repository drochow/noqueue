package api.jwt

import utils._
import org.joda.time.DateTime
import play.api.libs.json.{ Json, Format }

case class TokenPayload(
    val anwenderId: Long,
    val expiration: DateTime
) {
  Asserts.argumentIsNotNull(anwenderId)
  Asserts.argumentIsNotNull(expiration)
}

object TokenPayload {
  implicit val jsonFormat: Format[TokenPayload] = Json.format[TokenPayload]
}