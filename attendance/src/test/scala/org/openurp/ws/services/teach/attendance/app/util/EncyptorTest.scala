package org.openurp.ws.services.teach.attendance.app.util
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.FunSpec
import javax.crypto.BadPaddingException

/**
 * FIXME more testing
 */
@RunWith(classOf[JUnitRunner])
class EncyptorTest extends FunSpec {

  describe("Encyptor") {
    it("encrypt") {
      val str2 = new DesEncryptor("abcd1234").encrypt("2014004")
    }

    it("decrypt") {
      try {
        val str1 = new DesDecryptor("abcd1234").decrypt("e0ab7c54677385a8")
        this.fail("shouldn't decrypt success")
      } catch {
        case e: BadPaddingException =>
      }
      val str1 = new DesDecryptor("abcd1234").decrypt("3aaec16085006460")
    }
  }
}