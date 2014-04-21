package mbilski.spray.hmac

import spray.routing._
import spray.routing.directives.RouteDirectives.complete
import Directives._
import spray.testkit.ScalatestRouteTest
import spray.routing.AuthenticationFailedRejection.CredentialsRejected

class DirectivesSpec extends BaseSpec with ScalatestRouteTest {

  implicit var auth = new Auth(Some(Account("uid", "s")), Some("s"))

  def token(uuid: String, secret: String, url: String, method: String) = {
    addHeader("Authentication", hmac(uuid, secret, s"$method+$url"))
  }

  var route: Route = authenticate[Account] { account =>
    complete(account.email)
  }

  property("parses valid headers") {
    forAll { (uuid: String, hash: String) => whenever(List(uuid, hash).forall(_.matches("[\\S^!]+"))) {
      Directives.parseHeader(s"hmac $uuid:$hash").left.get should be(HmacData(uuid, hash))
    }}
  }

  property("rejects request without authentication header") {
    forAll { (header: String, value: String) => whenever(header != Directives.header) {
      Get("/") ~> addHeader(header, value) ~> route ~> check {
        rejection should be(MissingHeaderRejection(Directives.header))
      }
    }}
  }

  property("rejects request with invalid header format") {
    forAll { (value: String) => whenever(!value.matches(Directives.pattern)) {
      Get("/") ~> addHeader(Directives.header, value) ~> route ~> check {
        rejection should be(MalformedHeaderRejection(Directives.header, HmacException.InvalidFormat.msg))
      }
    }}
  }

  property("rejects request with invalid credentials") {
    forAll { (uuid: String, hash: String) => whenever(List(uuid, hash).forall(_.matches("[\\S^!]+"))) {
      Get("/") ~> addHeader(Directives.header, s"hmac $uuid:$hash") ~> route ~> check {
        rejection should be(AuthenticationFailedRejection(CredentialsRejected, List()))
      }
    }}
  }

  property("gets account") {
    Get("/") ~> token("uid", "s", "/", "GET") ~> route ~> check {
      responseAs[String] should be ("uid")
    }
  }

}
