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
      val str1 = new DesEncryptor(key).encrypt("2011121121")
      val str2 = new DesEncryptor(key).encrypt("20140308")
      val str3 = new DesEncryptor(key).encrypt("1659")
      println("&cardphyid=" + str1 + "&signindate=" + str2 + "&signintime=" + str3)
    }

    it("decrypt") {
      val str1 = new DesDecryptor(key).decrypt("80c252b0cd7b88fd8ea5cbeeb55d2029")
      println(str1)
    }
  }
}