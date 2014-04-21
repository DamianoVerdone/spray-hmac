package mbilski.spray.hmac

class SignerSpec extends BaseSpec {

  def validateTimestamp = {
    val t1 = Signer.timestamp
    val t2 = Signer.timestamp
    val t3 = Signer.previousTimestamp(t2)
    t1 should (equal(t2) or equal(t3))
  }

  property("generates the same timestamps") {
    0.to(1000).foreach(x => validateTimestamp)
  }

  property("generates the same hashes") {
    forAll { (secret: String, uri: String) =>
      whenever (notEmpty(secret, uri)) {
        Signer.generate(secret, uri, Signer.timestamp) should be(Signer.generate(secret, uri, Signer.timestamp))
      }
    }
  }

  property("generates unique hashes") {
    forAll { (s1: String, u1: String, s2: String, u2: String) =>
      whenever ( notEmpty(s1, u1, s2, u2) && s1 != s2 && u1 != u2) {
        Signer.generate(s1, u1, Signer.timestamp) should not be(Signer.generate(s2, u2, Signer.timestamp))
      }
    }
  }

  property("validates equals hashes") {
    forAll { (secret: String, uri: String) =>
      whenever (secret != "" && uri != "") {
        val hash = Signer.generate(secret, uri, Signer.timestamp)
        Signer.valid(hash, secret, uri) should be(true)
      }
    }
  }

  property("validates different hashes") {
    forAll { (s1: String, u1: String, s2: String, u2: String) =>
      whenever ( notEmpty(s1, u1, s2, u2) && s1 != s2 && u1 != u2) {
        val hash = Signer.generate(s1, u1, Signer.timestamp)
        Signer.valid(hash, s2, u2) should be(false)
      }
    }
  }

}
