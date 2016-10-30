package api.auth

import utils.Asserts
import play.api.libs.json.{Format, Json}

case class Credentials(
    email: String,
    password: String
) {
  Asserts.argumentIsNotNull(email)
  Asserts.argumentIsNotNull(password)
}

object Credentials {
  implicit val jsonFormat: Format[Credentials] = Json.format[Credentials]
}
