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
package org.openurp.ws.services.teach.attendance.app.model

import java.sql.Date

import org.beangle.commons.lang.Objects

/**
 * 考勤活动
 * <ul>
 * <li>id:非业务主键
 * <li>course:课程
 * <li>teacherName:教师姓名
 * <li>className:教学班名称
 * <li>courseDate:上课日期
 * <li>beginTime:上课开始时间
 * <li>endTime:上课结束时间
 * <li>attendBeginTime:考勤考试时间
 * </ul>
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class ActivityBean(val id: Long, val course: CourseBean, val teacherName: String, val className: String, val courseDate: Date, val beginTime: Int, val endTime: Int, val attendBeginTime: Int) {

  override def toString(): String =
    Objects.toStringBuilder(this).add("id", id).add("course", course)
      .add("teacherName", teacherName).add("className", className)
      .add("courseDate", courseDate)
      .add("beginTime", beginTime).add("endTime", endTime)
      .add("attendBeginTime", attendBeginTime).toString

  override def hashCode: Int = id.hashCode()
}