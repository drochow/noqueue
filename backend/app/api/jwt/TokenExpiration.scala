package api.jwt

import org.joda.time.Duration

/**
 * Created by David on 31.01.17.
 */
object TokenExpiration {
  val expirationDuration = 3600L * 1000 * 24
}
