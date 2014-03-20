package org.openurp.ws.services.teach.attendance.app.util
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSpec
import javax.crypto.BadPaddingException

@RunWith(classOf[JUnitRunner])
class EncyptorTest extends FunSpec {
  val key = "abcd1234"
  describe("Encyptor") {
    it("encrypt") {
      val str1 = new DesEncryptor(key).encrypt("2010141139")
      val str2 = new DesEncryptor(key).encrypt("20140320")
      val str3 = new DesEncryptor(key).encrypt("130000")
      println("&cardphyid=" + str1 + "&signindate=" + str2 + "&signintime=" + str3)
    }

    it("decrypt") {
      try {
        val str1 = new DesDecryptor(key).decrypt("e0ab7c54677385a8")
        this.fail("shouldn't decrypt success")
      } catch {
        case e: BadPaddingException =>
      }
      val str1 = new DesDecryptor(key).decrypt("3aaec16085006460")
    }
  }
}