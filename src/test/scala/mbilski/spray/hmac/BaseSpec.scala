package mbilski.spray.hmac

import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.PropertyChecks

trait BaseSpec extends PropSpec with PropertyChecks with Matchers {
  def notEmpty(s: String*) = !s.exists(_ == "")

  case class Account(email: String, secret: String)

  case class Auth(a: Option[Account], s: Option[String]) extends Authentication[Account] with DefaultSigner with SignerConfig {
    def accountAndSecret(uuid: String): (Option[Account], Option[String]) = (a, s)
  }

  def hmac(uuid: String, secret: String, uri: String) = s"hmac $uuid:${Signer.generate(secret, uri, Signer.timestamp)}"

}
