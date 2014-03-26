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
package org.openurp.ws.services.teach.attendance.app.domain

import java.sql.Date
import org.beangle.commons.lang.Strings.{ concat, leftPad }
import java.util.Calendar
import org.beangle.commons.lang.Dates._
/**
 * 分区策略
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
object ShardPolicy {

  /**
   * 活动表基于年
   */
  def activityPolicy(date: Date): (String, String) = ("t_attend_activities", String.valueOf(yearPostfix(date)))
  /**
   * 日志表按照年进行归档
   */
  def logPolicy(date: Date): (String, String) = ("t_attend_logs", String.valueOf(yearPostfix(date)))

  /**
   * 考勤表按照月进行归档
   */
  def detailPolicy(date: Date): (String, String) = ("t_attend_details", String.valueOf(yearMonthPostfix(date)))

  def activityTable(date: Date): String = {
    val p = activityPolicy(date)
    p._1 + p._2
  }

  def logTable(date: Date): String = {
    val p = logPolicy(date)
    p._1 + p._2
  }

  def detailTable(date: Date): String = {
    val p = detailPolicy(date)
    p._1 + p._2
  }

  private def yearMonthPostfix(date: Date): String = {
    val cal = toCalendar(date)
    val year = cal.get(Calendar.YEAR)
    val month = String.valueOf(cal.get(Calendar.MONTH) + 1)
    concat(year, leftPad(month, 2, '0'))
  }

  private def yearPostfix(date: Date): String = String.valueOf(toCalendar(date).get(Calendar.YEAR))

}
