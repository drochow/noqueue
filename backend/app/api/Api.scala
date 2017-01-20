package api

import play.api.i18n.Lang
import play.api.mvc.{ Call, RequestHeader }

/*
* Set of general values and methods for the API
*/
object Api {

  //////////////////////////////////////////////////////////////////////
  // Headers
  final val HEADER_CONTENT_TYPE = "Content-Type"
  final val HEADER_CONTENT_LANGUAGE = "Content-Language"
  final val HEADER_ACCEPT_LANGUAGE = "Accept-Language"
  final val HEADER_LOCATION = "Location"
  final val HEADER_AUTH_TOKEN = "X-Auth-Token"

  final val HEADER_PAGE = "X-Page"
  final val HEADER_PAGE_FROM = "X-Page-From"
  final val HEADER_PAGE_SIZE = "X-Page-Size"
  final val HEADER_PAGE_TOTAL = "X-Page-Total"

  /**
   * Basic headers
   * @param lang used language
   * @return
   */
  def basicHeaders(implicit lang: Lang) = Seq(
    HEADER_CONTENT_LANGUAGE -> lang.language,
    HEADER_ACCEPT_LANGUAGE -> lang.language
  )

  /**
   * Uses the given parameter to set header location
   *
   * @param uri uri that represents actuall location
   * @return
   */
  def locationHeader(uri: String): (String, String) = HEADER_LOCATION -> uri

  /**
   * Transforms call object to absolute URL and passes to not overloaded locationHeader
   *
   * @param call action call that will be transformed to a uri string
   * @param request request headers
   * @return
   */
  def locationHeader(call: Call)(implicit request: RequestHeader): (String, String) = locationHeader(call.absoluteURL())

  //////////////////////////////////////////////////////////////////////
  // Sorting
  object Sorting {
    final val ASC = false
    final val DESC = true
  }
}