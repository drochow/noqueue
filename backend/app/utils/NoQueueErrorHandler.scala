package utils

import java.sql.SQLException
import javax.inject._
import javax.security.auth.login.CredentialException

import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.{ I18nSupport, Lang, MessagesApi }
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router

import scala.concurrent._
import api.{ ApiError, ApiLog, ApiRequestHeaderImpl, ApiResult }
import api.ApiError._
import services.{ AdressNotFoundException, InvalidGeoCoordsException }

@Singleton
class NoQueueErrorHandler @Inject() (
    env: Environment,
    config: Configuration,
    sourceMapper: OptionalSourceMapper,
    router: Provider[Router],
    val messagesApi: MessagesApi
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) with I18nSupport {

  /**
   * 400 - Bad request. Called when a route is found, but it was not possible to bind the request parameters
   *
   * @param request
   * @param message
   * @return
   */
  override def onBadRequest(request: RequestHeader, message: String) =
    jsError(errorBadRequest(message)(request2Messages(request)), request)

  // 403 - Forbidden
  override def onForbidden(request: RequestHeader, message: String) =
    jsError(errorForbidden(request2Messages(request)), request)

  // 404 - Page not found error
  override def onNotFound(request: RequestHeader, message: String) =
    jsError(errorNotFound(request2Messages(request)), request)

  // 4XX - An error in the 4xx series, which is not handled by any of the other methods in this class already
  override def onOtherClientError(request: RequestHeader, statusCode: Int, message: String) =
    jsError(ApiError(statusCode, message), request)

  /**
   * 500 - Internal server error
   *
   * here we also catch common uncatched exceptions and handle and map them
   *
   * @param request
   * @param exception
   * @return
   */
  override def onServerError(request: RequestHeader, exception: Throwable) = {
    exception match {
      case mnae: MitarbeiterNotAnwesendException => jsError(errorBadRequest("This Mitarbeiter is not anwesend")(request2Messages(request)), request)
      case alue: AnwenderAlreadyLinedUpException => jsError(errorBadRequest("This Anwender already lined up somewhere")(request2Messages(request)), request)
      case dlie: DLInvalidException => jsError(errorBadRequest("This DL is not provided by this Mitarbeiter")(request2Messages(request)), request)
      case nse: WspDoesNotExistException => jsError(errorItemNotFound(request2Messages(request)), request)
      case nwowe: NotWorkingOnTisWSPException => jsError(errorBadRequest("You need to start working on this WSP first.")(request2Messages(request)), request)
      case aibe: AlreadWorkingOnAWspException => jsError(errorBadRequest("You need to finish all WSPs first.")(request2Messages(request)), request)
      case nfwspe: NotFirstWspException => jsError(errorBadRequest("You have to start with the first WSP.")(request2Messages(request)), request)
      case e: CredentialException => jsError(errorBadRequest("Invalid Credentials.")(request2Messages(request)), request)
      case anfe: AnwenderNotFoundException => jsError(errorItemNotFound("Anwender not found"), request)
      case bnaiue: BetriebNameAlreadyInUseException => jsError(errorBadRequest("Betrieb name already exists"), request)
      case btaiue: BetriebTelAlreadyInUseException => jsError(errorBadRequest("Betrieb tel already exists"), request)
      case dlaee: DLAlreadyExistsException => jsError(errorBadRequest("DL already exists"), request)
      case dlsiu: DLStillUsedInAWspException => jsError(errorBadRequest("DL still in use"), request)
      case ime: InvalidMitarbeiterException => jsError(errorBadRequest("Invalid Mitarbeiter"), request)
      case iwse: InvalidWspSubscribtionException => jsError(errorBadRequest("Invalid Subscription"), request)
      case laee: LeiterAlreadyExistsException => jsError(errorBadRequest("Leiter already exists"), request)
      case maee: MitarbeiterAlreadyExistsException => jsError(errorBadRequest("Mitarbeiter already exists"), request)
      case igce: InvalidGeoCoordsException => jsError(errorBadRequest(igce.getMessage)(request2Messages(request)), request)
      case anfe: AdressNotFoundException => jsError(errorItemNotFound("This adress does not exist.")(request2Messages(request)), request)
      case olre: OneLeiterRequiredException => jsError(errorBadRequest("Atleast 1 Leiter is required.")(request2Messages(request)), request)
      case ua: UnauthorizedException => jsError(errorUnauthorized(request2Messages(request)), request)
      case nse: NoSuchElementException => jsError(errorItemNotFound(request2Messages(request)), request)
      //Unexpected/Unhandled Exception
      case e: Exception => {
        ApiLog.error(exception) // log the exception
        jsError(errorInternalServer("Sorry, an internal error occured.")(request2Messages(request)), request) //show unexposed message to client
      }
    }
  }

  private def jsError(error: ApiError, request: RequestHeader): Future[Result] = Future.successful {
    error.saveLog(ApiRequestHeaderImpl(request)).toResult(request, request2Messages(request).lang)
  }

}