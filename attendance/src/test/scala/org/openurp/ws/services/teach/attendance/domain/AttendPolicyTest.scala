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
package org.openurp.ws.services.teach.attendance.domain

import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner
import org.openurp.ws.services.teach.attendance.model.AttendType._

@RunWith(classOf[JUnitRunner])
class AttendPolicyTest extends FunSpec with Matchers {
  describe("AttendTypePolicy") {
    it("Get or Set property") {
      val policy = new AttendTypePolicy
      policy.lateMax=15
      
      assert(policy.calcAttendType(719, 720, 750, 1000) == Unknown)
      assert(policy.calcAttendType(720, 720, 750, 1000) == Presence)
      assert(policy.calcAttendType(750, 720, 750, 1000) == Presence)
      assert(policy.calcAttendType(751, 720, 750, 1000) == Late)
      assert(policy.calcAttendType(805, 720, 750, 1000) == Late)
      assert(policy.calcAttendType(806, 720, 750, 1000) == Absenteeism)
      assert(policy.calcAttendType(1000, 720, 750, 1000) == Absenteeism)
      assert(policy.calcAttendType(1001, 720, 750, 1000) == Unknown)
    }
  }
}