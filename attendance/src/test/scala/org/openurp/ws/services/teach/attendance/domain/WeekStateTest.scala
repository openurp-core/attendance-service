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

import java.sql.Date
import java.util.Calendar
import org.beangle.commons.lang.Dates.toDate
import org.junit.runner.RunWith
import org.scalatest.{FunSpec, Matchers}
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TimeTest extends FunSpec with Matchers {
  describe("WeekStats") {
    it("build") {
      val cal = Calendar.getInstance()
      var rs = WeekStates.build(toDate(cal))
      println(rs)
      cal.setTime(Date.valueOf("2014-12-30"))
      rs = WeekStates.build(toDate(cal))
      assert(rs._2.size == 2)
      println(rs)

      cal.setTime(Date.valueOf("2014-01-05"))
      rs = WeekStates.build(toDate(cal))
      println(rs)
    }
  }
}