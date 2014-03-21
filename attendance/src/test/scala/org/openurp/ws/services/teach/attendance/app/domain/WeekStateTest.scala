package org.openurp.ws.services.teach.attendance.app.domain
import org.junit.runner.RunWith
import org.scalatest.FunSpec
import org.scalatest.Matchers
import org.openurp.ws.services.teach.attendance.app.model.AttendType
import org.scalatest.junit.JUnitRunner
import java.util.Calendar
import java.sql.Date

@RunWith(classOf[JUnitRunner])
class TimeTest extends FunSpec with Matchers {
  describe("WeekStats") {
    it("build") {
      val cal = Calendar.getInstance()
      var rs = WeekStates.build(cal)
      println(rs)
      cal.setTime(Date.valueOf("2014-12-30"))
      rs = WeekStates.build(cal)
      assert(rs._2.size == 2)
      println(rs)

      cal.setTime(Date.valueOf("2014-01-05"))
      rs = WeekStates.build(cal)
      println(rs)
    }
  }
}