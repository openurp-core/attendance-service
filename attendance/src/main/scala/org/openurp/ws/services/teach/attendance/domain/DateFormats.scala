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

import java.text.SimpleDateFormat
import java.{ util => ju }
import java.util.Calendar.{ HOUR_OF_DAY, MINUTE }
import org.beangle.commons.lang.Dates
import org.beangle.commons.lang.Numbers.toInt
import org.beangle.commons.lang.Strings.leftPad
import java.{util => ju}
/**
 * 日期时间工具类
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
object DateFormats {

  val dateFormat = new SimpleDateFormat("yyyyMMdd");
  val timeFormat = new SimpleDateFormat("HHmmss");
  val datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
  /**
   * 将日期类型转为"yyyyMMdd"
   *
   *  @param date
   *  @return
   */
  def toDateStr(date: ju.Date = new ju.Date()): String = {
    return dateFormat.format(date);
  }

  /**
   * 将时间类型转为“HHssmm”
   *
   *  @param date
   *  @return
   */
  def toTimeStr(date: ju.Date = new ju.Date()): String = {
    if (date == null) null
    else timeFormat.format(date);
  }

  def toDateTimeStr(date: ju.Date = new ju.Date()): String = {
    if (date == null) null
    else datetimeFormat.format(date);
  }

  def toCourseTime(time: ju.Calendar): Int = {
    val hour = String.valueOf(time.get(HOUR_OF_DAY))
    val min = String.valueOf(time.get(MINUTE))
    toInt(leftPad(hour, 2, '0') + leftPad(min, 2, '0'))
  }

  def toCourseTime(time: ju.Date): Int = toCourseTime(Dates.toCalendar(time))

  def toTimeStr(time: Int): String = {
    val t = leftPad(String.valueOf(time), 4, '0')
    return t.substring(0, 2) + ":" + t.substring(2)
  }
}