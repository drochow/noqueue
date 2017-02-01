package api

import play.api.libs.json.{ Format, Json }
import utils.Asserts

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
