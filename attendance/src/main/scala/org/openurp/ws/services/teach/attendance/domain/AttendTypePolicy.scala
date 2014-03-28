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
import org.beangle.commons.lang.Strings.leftPad
import org.beangle.commons.lang.Numbers.toInt
import org.openurp.ws.services.teach.attendance.model.AttendType._
/**
 * 出勤类型策略
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class AttendTypePolicy {

  /**迟到最大值(分钟)*/
  var lateMax: Int = 15

  def calcAttendType(signin: Int, info: SigninInfo): Int = calcAttendType(signin, info.attendBegin, info.begin, info.end)

  def calcAttendType(signin: Int, attendBegin: Int, begin: Int, end: Int): Int = {
    //早于考勤考试时间，不算
    if (signin < attendBegin) Unknown
    //考勤时间到上课时间之间的 出勤
    else if (signin <= begin) Presence
    //上课结束 不算
    else if (signin > end) Unknown
    else {
      // 迟到时间（分钟）
      if (toMinutes(signin) - toMinutes(begin) <= lateMax) Late
      // 上课15分之后(15<a)缺勤
      else Absenteeism
    }
  }
  private def toMinutes(time: Int): Int = {
    var timeStr = String.valueOf(time)
    val time4 = leftPad(timeStr, 4, '0')
    toInt(time4.substring(0, 2)) * 60 + toInt(time4.substring(2, 4))
  }
}