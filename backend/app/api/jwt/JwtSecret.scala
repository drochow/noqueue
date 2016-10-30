package api.jwt

import utils.Asserts
import com.nimbusds.jose.crypto.{ MACVerifier, MACSigner }

case class JwtSecret(secret: String) {
  Asserts.argumentIsNotNullNorEmpty(secret)

  val signer = new MACSigner(secret)
  val verifier = new MACVerifier(secret)
}
