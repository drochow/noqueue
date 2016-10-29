package api.jwt

import utils.Asserts
import org.joda.time.DateTime
import play.api.libs.json.{Json, Format}

case class TokenPayload(
  userId      : Long,
  email       : String,
  expiration  : DateTime
) {
  Asserts.argumentIsNotNull(userId)
  Asserts.argumentIsNotNull(email)
  Asserts.argumentIsNotNull(expiration)
}

object TokenPayload
{
  implicit val jsonFormat : Format[TokenPayload] = Json.format[TokenPayload]
}