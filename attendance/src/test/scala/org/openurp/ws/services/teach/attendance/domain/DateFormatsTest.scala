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

import java.util.Calendar
import org.junit.runner.RunWith
import org.openurp.ws.services.teach.attendance.domain.DateFormats._
import org.scalatest.FunSpec
import org.beangle.commons.lang.Dates
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DateFormatsTest extends FunSpec {
  describe("DateUtils") {
    it("format") {
      val sqlDate = java.sql.Date.valueOf("2009-03-06")
      println(toDateTimeStr(sqlDate))
      println(toDateTimeStr(Dates.toDate(Calendar.getInstance)))
    }
    it("toCourseTime") {
      println(toCourseTime(Dates.now))
    }
  }
}