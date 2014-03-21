package org.openurp.ws.services.teach.attendance.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import org.beangle.commons.lang.Strings._
import org.beangle.commons.lang.Numbers._
object DateUtils {

  val dateFormat = new SimpleDateFormat("yyyyMMdd");
  val timeFormat = new SimpleDateFormat("HHmmss");
  val datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
  /**
   * 将日期类型转为"yyyyMMdd"
   *
   *  @param date
   *  @return
   */
  def toDateStr(date: Date = new Date()): String = {
    return dateFormat.format(date);
  }

  /**
   * 将时间类型转为“HHssmm”
   *
   *  @param date
   *  @return
   */
  def toTimeStr(date: Date = new Date()): String = {
    if (date == null) null
    else timeFormat.format(date);
  }

  def toDateTimeStr(date: Date = new Date()): String = {
    if (date == null) null
    else datetimeFormat.format(date);
  }
  
  def toCalendar(dateStr: String): Calendar = {
    val cal = Calendar.getInstance()
    cal.setTime(java.sql.Date.valueOf(dateStr))
    cal
  }
  
  def join(date: java.sql.Date, time: java.sql.Time): Calendar = {
    val cal = Calendar.getInstance()
    cal.setTime(date)
    cal.set(Calendar.HOUR_OF_DAY, time.getHours)
    cal.set(Calendar.MINUTE, time.getMinutes)
    cal.set(Calendar.SECOND, 0)
    cal
  }

  //FIXME with out Test
  def toCourseTime(time: java.util.Calendar): Int = {
    val hour = String.valueOf(time.get(Calendar.HOUR_OF_DAY))
    val min = String.valueOf(time.get(Calendar.MINUTE))
    toInt(leftPad(hour, 2, '0') + leftPad(min, 2, '0'))
  }

  def toCourseTime(time: java.util.Date): Int = {
    val cal = Calendar.getInstance
    cal.setTime(time)
    toCourseTime(cal)
  }

  def toTimeStr(time: Int): String = {
    val t = leftPad(String.valueOf(time), 4, '0')
    return t.substring(0, 2) + ":" + t.substring(2)
  }
}