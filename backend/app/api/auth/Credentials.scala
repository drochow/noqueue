package api.auth

import utils.Asserts
import play.api.libs.json.{ Format, Json }

case class Credentials(
    nutzerName: String,
    password: String
) {
  Asserts.argumentIsNotNull(nutzerName)
  Asserts.argumentIsNotNull(password)
}

object Credentials {
  implicit val jsonFormat: Format[Credentials] = Json.format[Credentials]
}
