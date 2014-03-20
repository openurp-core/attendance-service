package org.openurp.ws.services.teach.attendance.app.domain

import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.junit.JUnitRunner
import org.scalatest.Matchers
import org.openurp.ws.services.teach.attendance.app.model.AttendType

@RunWith(classOf[JUnitRunner])
class AttendPolicyTest extends FunSpec with Matchers {
  describe("AttendTypePolicy") {
    it("Get or Set property") {
      val policy = new AttendTypePolicy
      assert(policy.calcAttendType(719, 750, 1000) == AttendType.Unknown)
      assert(policy.calcAttendType(720, 750, 1000) == AttendType.Presence)
      assert(policy.calcAttendType(750, 750, 1000) == AttendType.Presence)
      assert(policy.calcAttendType(751, 750, 1000) == AttendType.Late)
      assert(policy.calcAttendType(805, 750, 1000) == AttendType.Late)
      assert(policy.calcAttendType(806, 750, 1000) == AttendType.Absenteeism)
      assert(policy.calcAttendType(1000, 750, 1000) == AttendType.Absenteeism)
      assert(policy.calcAttendType(1001, 750, 1000) == AttendType.Unknown)
    }
  }
}