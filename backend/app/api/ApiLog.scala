package api

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Logger
import play.api.libs.json.{ JsNull, JsValue, Json }
import play.api.mvc.RequestHeader

/**
 * Api Logging Component to log ApiRequest and occuring exceptions
 */

/**
 * Api-Log representation
 *
 * @param date
 * @param ip
 * @param token
 * @param method
 * @param uri
 * @param requestBody
 * @param responseStatus
 * @param responseBody
 */
case class ApiLog(
    date: DateTime,
    ip: String,
    token: Option[String],
    method: String,
    uri: String,
    requestBody: Option[String],
    responseStatus: Int,
    responseBody: Option[String]
) {
  def dateStr: String = ApiLog.dateTimeFormat.print(date)

  override def toString: String = {
    val tokenEntry: String = if (!token.isEmpty) s" [token: ${token.get.substring(0, 10)}...]" else ""
    val requestBodyEntry: String = if (!requestBody.isEmpty) s" [requestBody: ${requestBody.get.split('\n').map(_.trim).mkString}]" else ""
    val responseBodyEntry: String = if (!responseBody.isEmpty) s" [requestBody: ${responseBody.get.split('\n').map(_.trim).mkString}]" else ""
    s"[$dateStr] [ip: $ip] [uri: $uri] [method: $method]$tokenEntry$requestBodyEntry [responseStatus: $responseStatus]$responseBodyEntry"
  }

}

object ApiLog {

  private val dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:ss:mm")
  private val logger = Logger(this.getClass)

  /**
   * Creates Log ApiLog entry
   *
   * @param request
   * @param status
   * @param json
   * @param level
   * @tparam R
   */
  def log[R <: RequestHeader](request: ApiRequestHeader[R], status: Int, json: JsValue, level: String = "info") = {
    val logEntry = ApiLog(
      date = request.now,
      ip = request.remoteAddress,
      token = request.tokenOpt,
      method = request.method,
      uri = request.uri,
      requestBody = request.maybeBody,
      responseStatus = status,
      responseBody = if (json == JsNull) None else Some(Json.prettyPrint(json))
    )
    level match {
      case "debug" => this.logger.debug(logEntry.toString)
      case "error" => this.logger.error(logEntry.toString)
      case "info" => this.logger.info(logEntry.toString)
      //if an invalid/unknown log level is provided we assume the logleven "Info"
      case _ => this.logger.info(logEntry.toString)
    }
  }

  /**
   * Exception Logging
   *
   * @param e
   */
  //@todo transverse stack trace to stirng  with throwable.getstacktrace
  def error(e: Throwable) = {
    val date = dateTimeFormat.print(DateTime.now())
    this.logger.error(s"[$date] [exceptionMessage: ${e.getMessage}] [exceptionStack: ${e.getStackTraceString}]")
  }

}
