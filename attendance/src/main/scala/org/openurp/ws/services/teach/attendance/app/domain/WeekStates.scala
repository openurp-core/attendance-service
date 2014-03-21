package org.openurp.ws.services.teach.attendance.app.domain

import java.lang.Long.valueOf
import java.util.Calendar
import java.util.Calendar.{ DAY_OF_WEEK, SUNDAY, WEEK_OF_YEAR, YEAR }

import org.beangle.commons.lang.Strings.repeat

object WeekStates {
  def build(cal: Calendar): (Int, List[(Int, Long, String)]) = {
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