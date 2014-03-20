package org.openurp.ws.services.teach.attendance.app.domain
import org.beangle.commons.lang.Strings.leftPad
import org.beangle.commons.lang.Numbers.toInt
import org.openurp.ws.services.teach.attendance.app.model.AttendType._

class AttendTypePolicy {
  
  /**迟到最大值(分钟)*/
  var lateMax: Int = 15

  /**提前最大值(分钟)*/
  var earlyMax: Int = -30

  def calcAttendType(signin: Int, begin: Int, end: Int): Int = {
    val beginMin = toMinutes(begin)
    val delta = toMinutes(signin) - beginMin
    val courseHour = toMinutes(end) - beginMin
    //提前30分钟以上 不算出勤
    if (delta < earlyMax) Unknown
    //提前30分钟以内(30<=a<=0) 出勤
    else if (delta <= 0) Presence
    //上课15分之内的(0<a<=15) 迟到
    else if (delta <= lateMax) Late
    //上课15之后(15<a<=courseHour)缺勤
    else if (delta <= courseHour) Absenteeism
    else Unknown
  }

  private def toMinutes(time: Int): Int = {
    var timeStr = String.valueOf(time)
    val time4 = leftPad(timeStr, 4, '0')
    toInt(time4.substring(0, 2)) * 60 + toInt(time4.substring(2, 4))
  }
}