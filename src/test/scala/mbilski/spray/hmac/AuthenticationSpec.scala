package mbilski.spray.hmac

class AuthenticationSpec extends BaseSpec {

  property("returns account when account exists and hashes match") {
    forAll {(uuid: String, secret: String, uri: String) =>
      whenever(notEmpty(uuid, secret, uri)) {
        val hash = Signer.generate(secret, uri, Signer.timestamp)
        val auth = Auth(Some(Account(uuid, secret)), Some(secret))
        auth.authenticate(HmacData(uuid, hash), uri).get should be(Account(uuid, secret))
      }
    }
  }

  property("returns none when account or secret does not exist") {
    forAll { (uuid: String, secret: String, uri: String) =>
      whenever(notEmpty(uuid, secret, uri)) {
        val hash = Signer.generate(secret, uri, Signer.timestamp)
        Auth(None, None).authenticate(HmacData(uuid, hash), uri) should be(None)
        Auth(Some(Account(uuid, secret)), None).authenticate(HmacData(uuid, hash), uri) should be(None)
        Auth(None, Some(secret)).authenticate(HmacData(uuid, hash), uri) should be(None)
      }
    }
  }

}
