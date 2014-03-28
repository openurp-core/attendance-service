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
import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EncyptorTest extends FunSpec {
  val key = "abcd1234"
  describe("Encyptor") {
    it("encrypt") {
      val str1 = new DesEncryptor(key).encrypt("2013121202")
      val str2 = new DesEncryptor(key).encrypt("20140327")
      val str3 = new DesEncryptor(key).encrypt("082100")
      println("&cardphyid=" + str1 + "&signindate=" + str2 + "&signintime=" + str3)
    }
//    it("decrypt") {
//      val str1 = new DesDecryptor(key).decrypt("80c252b0cd7b88fd8ea5cbeeb55d2029")
//      println(str1)
//    }
  }
}