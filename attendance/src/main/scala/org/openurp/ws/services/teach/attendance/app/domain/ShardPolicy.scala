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
   * 日志表按照年进行归档
   */
  def logTableName(date: Date): String = "t_attend_logs" + logPostfix(date)

  /**
   * 考勤表按照月进行归档
   */
  def detailTableName(date: Date): String = "t_attend_details" + detailPostfix(date)

  def detailPostfix(date: Date): String = {
    val cal = toCalendar(date)
    val year = cal.get(Calendar.YEAR)
    val month = String.valueOf(cal.get(Calendar.MONTH) + 1)
    concat(year, leftPad(month, 2, '0'))
  }

  def logPostfix(date: Date): String = String.valueOf(toCalendar(date).get(Calendar.YEAR))

}
