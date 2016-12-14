package models

import api.ApiRequestHeader
import play.api.mvc.RequestHeader
import play.api.libs.json._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/*
* Stores all the information of a request. Specially used for store the errors in the DB.
*/
case class ApiLog(
    id: Long,
    date: DateTime,
    ip: String,
    token: Option[String],
    method: String,
    uri: String,
    requestBody: Option[String],
    responseStatus: Int,
    responseBody: Option[String]
) {
  def dateStr: String = ApiLog.dtf.print(date)
}
object ApiLog {
  private val dtf = DateTimeFormat.forPattern("MM/dd/yyyy HH:ss:mm")
}