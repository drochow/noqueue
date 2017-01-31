package api

import api.ApiError._
import api.Api.Sorting._
import api.jwt.{ JwtSecret, JwtUtil, TokenPayload }
import org.joda.time.DateTime
import play.api.Configuration
import play.api.mvc._
import javax.inject.Inject

import models.{ DB, PostgresDB, UnregistrierterAnwender }
import models.db.{ BetriebEntity, DAL, PK }

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.i18n.{ I18nSupport, MessagesApi }

import scala.util.{ Failure, Success, Try }
import play.api.libs.json._
import play.api.inject.ApplicationLifecycle

//@todo think about separating commonerApiC and inheriting SecuredApiC

/**
 * Controller trait for API controllers
 */
trait ApiController extends Controller with I18nSupport {
  val dbD: DB;
  val config: Configuration;
  val messagesApi: MessagesApi
  implicit protected val SECRET: JwtSecret = JwtSecret(config.getString("jwt.token.secret").get);
  ////////////////////////////////////
  /// Transformation Uitlities
  ////////////////////////////////////

  /**
   * Transforms an Object to JSON
   *
   * @param o Generic Object
   * @param tjs JsWriter
   * @tparam T Type of Generic Object
   * @return
   */
  implicit def objectToJson[T](o: T)(implicit tjs: Writes[T]): JsValue = Json.toJson(o)

  /**
   * Transforms a result to a future
   *
   * @param r Result Object
   * @return
   */
  implicit def result2FutureResult(r: ApiResult): Future[ApiResult] = Future.successful(r)

  //////////////////////////////////////////////////////////////////////
  // Custom Actions
  //////////////////////////////////////////////////////////////////////

  /**
   * Simple API-Action
   *
   * @param action callback action
   * @return
   */
  def ApiAction(action: ApiRequest[Unit] => Future[ApiResult]) = ApiActionWithParser(parse.empty)(action)

  /**
   * Simple API-Action that requires a Body
   *
   * @param action callback action
   * @return
   */
  def ApiActionWithBody(action: ApiRequest[JsValue] => Future[ApiResult]) = ApiActionWithParser(parse.json)(action)

  /**
   * Api Action that requires authentication
   * @param action callback action
   * @return
   */
  def SecuredApiAction(action: SecuredAnwenderApiRequest[Unit] => Future[ApiResult]) = SecuredApiActionWithParser(parse.empty)(action)

  /**
   * Api Action that requires authentication and a Body
   *
   * @param action callback action
   * @return
   */
  def SecuredApiActionWithBody(action: SecuredAnwenderApiRequest[JsValue] => Future[ApiResult]) = SecuredApiActionWithParser(parse.json)(action)

  /**
   * Leiter Api Action that requires authentication
   * @param action callback action
   * @return
   */
  def SecuredLeiterApiAction(betriebId: PK[BetriebEntity])(action: SecuredLeiterApiRequest[Unit] => Future[ApiResult]) = SecuredLeiterApiActionWithParser(betriebId)(parse.empty)(action)

  /**
   * Leiter Api Action that requires authentication and a Body
   *
   * @param action callback action
   * @return
   */
  def SecuredLeiterApiActionWithBody(betriebId: PK[BetriebEntity])(action: SecuredLeiterApiRequest[JsValue] => Future[ApiResult]) = SecuredLeiterApiActionWithParser(betriebId)(parse.json)(action)

  /**
   * Leiter Api Action that requires authentication
   * @param action callback action
   * @return
   */
  def SecuredMitarbeiterApiAction(betriebId: PK[BetriebEntity])(action: SecuredMitarbeiterApiRequest[Unit] => Future[ApiResult]) = SecuredMitarbeiterApiActionWithParser(betriebId)(parse.empty)(action)

  /**
   * Leiter Api Action that requires authentication and a Body
   *
   * @param action callback action
   * @return
   */
  def SecuredMitarbeiterApiActionWithBody(betriebId: PK[BetriebEntity])(action: SecuredMitarbeiterApiRequest[JsValue] => Future[ApiResult]) = SecuredMitarbeiterApiActionWithParser(betriebId)(parse.json)(action)

  /**
   * Creates an Action checking that the Request has all the common necessary headers with their correct values
   *
   * @param parser body parser to parse request body
   * @param action callback action
   * @tparam A type of body data
   * @return
   */
  private def ApiActionCommon[A](parser: BodyParser[A])(action: (ApiRequest[A]) => Future[ApiResult]) = Action.async(parser) { implicit request =>

    implicit val apiRequest = ApiRequest(request)

    val futureApiResult: Future[ApiResult] = action(apiRequest)

    futureApiResult.map {
      case error: ApiError => error.saveLog.toResult
      case response: ApiResponse => response.saveLog.toResult
    }

  }

  /**
   * Basic API action wich requries (actually) no additional headers
   *
   * @param parser bodyparser that parses the request body
   * @param action callback action
   * @tparam A Type of body data
   * @return
   */
  private def ApiActionWithParser[A](parser: BodyParser[A])(action: ApiRequest[A] => Future[ApiResult]) = ApiActionCommon(parser) { (apiRequest) =>
    action(apiRequest)
  }

  private def payloadExtractor[A](apiRequest: ApiRequest[A], action: (TokenPayload, UnregistrierterAnwender) => Future[ApiResult]): Future[ApiResult] = {
    apiRequest.tokenOpt match {
      case None => errorTokenNotFound
      case Some(token) => JwtUtil.getPayloadIfValidToken[TokenPayload](token).flatMap {
        case None => errorTokenUnknown
        case Some(payload) => action(payload, new UnregistrierterAnwender(dbD))
      }
    }
  }

  /**
   * Secured API action that verifies an Anwender
   *
   * @param parser bodyparser that parses the request body
   * @param action callback action
   * @tparam A Type of body data
   * @return
   */
  private def SecuredApiActionWithParser[A](parser: BodyParser[A])(action: SecuredAnwenderApiRequest[A] => Future[ApiResult]) = ApiActionCommon(parser) { (apiRequest) =>
    payloadExtractor(
      apiRequest,
      (payload, uAnw) => {
        action(SecuredAnwenderApiRequest(apiRequest.request, uAnw.anmeldenMitPayload(payload)))
      }
    )
  }

  //  private def ActionWithPayload[A](action: ApiRequest[A] => Future[ApiResult])

  /**
   * Secured API action that verifies a Leiter
   *
   * @param parser bodyparser that parses the request body
   * @param action callback action
   * @tparam A Type of body data
   * @return
   */
  private def SecuredLeiterApiActionWithParser[A](betriebId: PK[BetriebEntity])(parser: BodyParser[A])(action: SecuredLeiterApiRequest[A] => Future[ApiResult]) = ApiActionCommon(parser) { (apiRequest) =>
    payloadExtractor(
      apiRequest,
      (payload, uAnw) => {
        action(SecuredLeiterApiRequest(apiRequest.request, uAnw.anmeldenMitPayloadAlsLeiterVon(payload, betriebId)))
      }
    )
  }

  /**
   * Secured API action that verifies a Mitarbeiter
   *
   * @param parser bodyparser that parses the request body
   * @param action callback action
   * @tparam A Type of body data
   * @return
   */
  private def SecuredMitarbeiterApiActionWithParser[A](betriebId: PK[BetriebEntity])(parser: BodyParser[A])(action: SecuredMitarbeiterApiRequest[A] => Future[ApiResult]) = ApiActionCommon(parser) { (apiRequest) =>
    payloadExtractor(
      apiRequest,
      (payload, uAnw) => {
        action(SecuredMitarbeiterApiRequest(apiRequest.request, uAnw.anmeldenMitPayloadAlsMitarbeiterVon(payload, betriebId)))
      }
    )
  }

  //////////////////////////////////////////////////////////////////////
  // Auxiliar methods to create ApiResults from writable JSON objects

  /**
   * Auxiliar method that creates an API Result
   *
   * @param obj writable json object
   * @param headers headers to be send
   * @param w json serialization object
   * @tparam A type of write object
   */
  def ok[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.ok(obj, headers: _*))

  /**
   *
   * @param futObj future object
   * @param headers headers to be send
   * @param w json serialization object
   * @tparam A type to write
   * @return
   */
  def okF[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.ok(obj, headers: _*))

  /**
   *
   *
   * @param obj response object
   * @param headers headers to be send
   * @param w json serialization object
   * @tparam A type of response object
   * @return
   */
  def created[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.created(obj, headers: _*))

  /**
   *
   * @param futObj future response object
   * @param headers headers to be send
   * @param w json serialization object
   * @tparam A type of response object
   * @return
   */
  def created[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.created(obj, headers: _*))

  /**
   * @param headers headers to be send
   * @return
   */
  def created(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.created(headers: _*))

  /**
   *
   * @param obj response object
   * @param headers headers to be send
   * @param w json serialization object
   * @tparam A type of response object
   * @return
   */
  def accepted[A](obj: A, headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = Future.successful(ApiResponse.accepted(obj, headers: _*))

  /**
   *
   * @param futObj future response object
   * @param headers headers to be send
   * @param w json serialization object
   * @tparam A type of future response object
   * @return
   */
  def accepted[A](futObj: Future[A], headers: (String, String)*)(implicit w: Writes[A]): Future[ApiResult] = futObj.map(obj => ApiResponse.accepted(obj, headers: _*))

  /**
   *
   * @param headers headers to be send
   * @return
   */
  def accepted(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.accepted(headers: _*))

  /**
   *
   * @param headers headers to be send
   * @return
   */
  def noContent(headers: (String, String)*): Future[ApiResult] = Future.successful(ApiResponse.noContent(headers: _*))

  //////////////////////////////////////////////////////////////////////
  // More auxiliar methods

  /**
   * Reads an object from an ApiRequest[JsValue] handling a possible malformed error
   *
   * @param f future api result
   * @param request Api Request
   * @param rds json deserializer
   * @param req request headers
   * @tparam T type of api result object
   * @return
   */
  def readFromRequest[T](f: T => Future[ApiResult])(implicit request: ApiRequest[JsValue], rds: Reads[T], req: RequestHeader): Future[ApiResult] = {
    request.body.validate[T].fold(
      errors => errorBodyMalformed(errors),
      readValue => f(readValue)
    )
  }

}