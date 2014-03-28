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

import java.lang.Long.valueOf
import java.sql.Date
import java.util.Calendar
import java.util.Calendar.{ DAY_OF_WEEK, SUNDAY, WEEK_OF_YEAR, YEAR }

import org.beangle.commons.lang.Dates.toCalendar
import org.beangle.commons.lang.Strings.repeat

/**
 * 周状态构建对象（兼容原有系统的方式）
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
object WeekStates {
  def build(date: Date): (Int, List[(Int, Long, String)]) = {
    val cal = toCalendar(date)
    cal.setFirstDayOfWeek(SUNDAY)
    val weekday = if (cal.get(DAY_OF_WEEK) == 1) 7 else cal.get(DAY_OF_WEEK) - 1
    val year = cal.get(YEAR)
    val weekStateBuf = new StringBuilder(repeat('0', 53))
    var weekIndex = cal.get(WEEK_OF_YEAR)
    weekStateBuf.setCharAt(weekIndex - 1, '1')
    var weekStats = new collection.mutable.ListBuffer[(Int, Long, String)]
    weekStats += ((cal.get(YEAR), valueOf(weekStateBuf.mkString, 2), weekStateBuf.mkString))
    if (weekIndex == 1 && weekday != 7) {
      val lastWeekState = repeat('0', 52) + '1'
      weekStats += Tuple3((cal.get(Calendar.YEAR) - 1), valueOf(lastWeekState, 2), lastWeekState)
    }
    (weekday, weekStats.toList)
  }
}