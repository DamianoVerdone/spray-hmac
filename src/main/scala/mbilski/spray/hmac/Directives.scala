package mbilski.spray.hmac

import spray.routing._
import spray.routing.directives.MiscDirectives._
import spray.routing.directives.RouteDirectives._
import spray.routing.AuthenticationFailedRejection.CredentialsRejected
import spray.routing.MalformedHeaderRejection
import spray.routing.MissingHeaderRejection

case class HmacException(msg: String)

object HmacException {
  val InvalidFormat = HmacException("Invalid hmac format")
}

trait HmacConfig {
  val pattern = """^hmac (\S+):(\S+)$"""
  val header = "Authentication"
  val regex = pattern.r
}

trait Directives { this: HmacConfig =>
  def parseHeader(header: String): Either[HmacData, HmacException] = header match {
    case regex(uuid, hash) => Left(HmacData(uuid, hash))
    case _ => Right(HmacException.InvalidFormat)
  }

  def authenticate[A](block: A => Route)(implicit auth: Authentication[A]): Route = requestInstance { request =>
    request.headers.find(_.name == header).map(h =>
      parseHeader(h.value).fold(verify(_, s"${request.method}+${request.uri.path}", block), ex => invalidFormat(ex.msg))
    ).getOrElse(missingHeader)
  }

  def verify[A](hmacData: HmacData, uri: String, block: A => Route)(implicit auth: Authentication[A]): Route = {
    auth.authenticate(hmacData, uri).map(account => block(account)).getOrElse(invalidCredentials)
  }

  def missingHeader = reject(MissingHeaderRejection(header))
  def invalidFormat(msg: String) = reject(MalformedHeaderRejection(header, msg))
  def invalidCredentials = reject(AuthenticationFailedRejection(CredentialsRejected, List()))
}

object Directives extends Directives with HmacConfig
