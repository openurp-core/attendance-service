/*
 * OpenURP, Open University Resouce Planning
 *
 * Copyright (c) 2013-2014, OpenURP Software.
 *
 * OpenURP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenURP is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Beangle.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.ws.services.teach.attendance.web.util

import org.beangle.commons.bean.Factory
import org.beangle.commons.lang.{ Strings, SystemInfo }

import javax.crypto.{ Cipher, SecretKeyFactory }
import javax.crypto.spec.{ DESKeySpec, IvParameterSpec }

/**
 * Decryptor based on DES
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
final class DesDecryptor(val key: String) {
  val desKeySpec = new DESKeySpec(key.getBytes("UTF-8"))
  val deskey = SecretKeyFactory.getInstance("DES").generateSecret(desKeySpec)
  val paramKeySpec = new IvParameterSpec(key.getBytes("UTF-8"))

  def decrypt(message: String): String = {
    //不能使用在多线程环境中,每次解密时创建一个
    val cipher = Cipher.getInstance("DES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, deskey, paramKeySpec)
    new String(cipher.doFinal((hexToBytes(message))), "UTF-8")
  }

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
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
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