package org.openurp.ws.services.teach.attendance.app.util
import javax.crypto.SecretKeyFactory
import sun.misc.BASE64Decoder
import javax.crypto.spec.DESKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import org.beangle.commons.bean.Factory
import org.beangle.commons.lang.SystemInfo
import org.beangle.commons.lang.Strings
import org.beangle.commons.lang.time.Stopwatch

/**
 * Decryptor based on DES
 */
final class DesDecryptor(val key: String) {
  def decrypt(message: String): String = new String(cipher.doFinal((hexToBytes(message))), "UTF-8")

  private val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
  cipher.init(Cipher.DECRYPT_MODE, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes("UTF-8"))),
    new IvParameterSpec(key.getBytes("UTF-8")))

  private def hexToBytes(str: String): Array[Byte] = {
    if (str == null || str.equals("")) null
    else {
      var hexString = str.toUpperCase();
      val length = hexString.length() / 2;
      val hexChars = hexString.toCharArray();
      var d = new Array[Byte](length)
      var i = 0
      while (i < length) {
        val pos = i * 2;
        d(i) = (charToByte(hexChars(pos)) << 4 | charToByte(hexChars(pos + 1))).asInstanceOf[Byte]
        i += 1
      }
      d
    }
  }
  private def charToByte(c: Char): Byte = "0123456789ABCDEF".indexOf(c).asInstanceOf[Byte]

}

/**
 * Encryptor Based on DES
 */
final class DesEncryptor(val key: String) {

  def encrypt(message: String): String = toHexString(cipher.doFinal(message.getBytes("UTF-8")))

  private val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
  cipher.init(Cipher.ENCRYPT_MODE, SecretKeyFactory.getInstance("DES").generateSecret(new DESKeySpec(key.getBytes("UTF-8"))),
    new IvParameterSpec(key.getBytes("UTF-8")))

  private def toHexString(b: Array[Byte]): String = {
    val hexString = new StringBuffer()
    var i = 0
    while (i < b.length) {
      var plainText = Integer.toHexString(0xff & b(i));
      if (plainText.length() < 2)
        plainText = "0" + plainText;
      hexString.append(plainText);
      i += 1
    }
    hexString.toString()
  }
}

class DesDecryptorFactory extends Factory[DesDecryptor] {

  def getObject: DesDecryptor = {
    SystemInfo.properties.get("desc_secret_key") match {
      case Some(key) => new DesDecryptor(key)
      case None => throw new RuntimeException("Cannot find system properties desc_secrity_key")
    }
  }
  def getObjectType: Class[DesDecryptor] = classOf[DesDecryptor]

  def singleton: Boolean = true
}

class DesEncryptorFactory extends Factory[DesEncryptor] {

  def getObject: DesEncryptor = {
    SystemInfo.properties.get("desc_secret_key") match {
      case Some(key) => new DesEncryptor(key)
      case None => throw new RuntimeException("Cannot find system properties desc_secrity_key")
    }
  }

  def getObjectType: Class[DesEncryptor] = classOf[DesEncryptor]

  def singleton: Boolean = true
}

class DecryptTransformer(val decryptor: DesDecryptor) extends Transformer {
  def transform(value: String): TransformerResult = {
    var rs: String = null
    var msg: String = null
    try {
      rs = decryptor.decrypt(value)
    } catch {
      case e: Exception => msg = "无法解密:" + e.getMessage
    }
    if (Strings.isEmpty(rs)) {
      rs = value
      msg = "无法解密:" + rs
    }
    new TransformerResult(rs, msg)
  }

  def resultType: Class[_] = classOf[String]
}