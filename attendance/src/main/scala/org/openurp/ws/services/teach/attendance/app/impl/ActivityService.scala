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
package org.openurp.ws.services.teach.attendance.app.impl

import java.{ util => ju }

import org.beangle.commons.bean.Initializing
import org.beangle.commons.cache.{ Cache, CacheManager }
import org.beangle.commons.lang.Dates.toDate
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.model.{ ActivityBean, Classroom }
import org.openurp.ws.services.teach.attendance.app.util.DateUtils.toCourseTime
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy._
/**
 * 考勤活动服务
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class ActivityService extends Logging with Initializing {
  var executor: JdbcExecutor = _
  var cacheManager: CacheManager = _
  var baseDataService: BaseDataService = _
  private var cache: Cache[Long, ActivityBean] = _

  def getActivity(room: Classroom, datetime: ju.Date): Option[ActivityBean] = {
    var rs: Option[ActivityBean] = None
    val date = toDate(datetime)
    baseDataService.getSemesterId(date) foreach { semesterId =>
      val datas = executor.query("select aa.id from " + activityTable(date) + "  aa " +
        " where aa.semester_id =? and aa.course_date=? and ? between aa.attend_begin_time and aa.end_time and aa.room_id=?",
        semesterId, date, toCourseTime(datetime), room.id)
      datas.foreach { data =>
        val activityId = data.head.asInstanceOf[Number].longValue
        rs = cache.get(activityId)
        if (rs.isEmpty) {
          val datas = executor.query("select aa.id,aa.lesson_id,aa.course_id,rw.jxbmc class_name,aa.begin_time,aa.end_time,aa.attend_begin_time " +
            " from " + activityTable(date) + " aa,jxrw_t rw where aa.id=? and aa.lesson_id=rw.id", activityId)
          datas foreach { data =>
            val activityId = data(0).asInstanceOf[Number].longValue()
            val course = baseDataService.getCourse(data(2).asInstanceOf[Number])
            val teacherIds = executor.query("select t.LSID from JXRW_LS_T t where t.jxrwid=?", data(1).asInstanceOf[Number])
            val teacherNames = new collection.mutable.ListBuffer[String]
            teacherIds foreach { teacherId =>
              teacherNames += baseDataService.getTeacherName(teacherId.head.asInstanceOf[Number])
            }
            val teacherName = teacherNames.mkString(" ")
            val className = data(3).toString
            val beginTime = data(4).asInstanceOf[Number].intValue
            val endTime = data(5).asInstanceOf[Number].intValue
            val attendBeginTime = data(6).asInstanceOf[Number].intValue
            val lesson = new ActivityBean(activityId, course.get, teacherName, className, date, beginTime, endTime, attendBeginTime)
            cache.put(activityId, lesson)
            rs = Some(lesson)
          }
        }
      }
    }
    rs
  }
  def init() {
    cache = cacheManager.getCache("lesson")
  }
}