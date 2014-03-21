package org.openurp.ws.services.teach.attendance.app.impl

import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor
import org.beangle.commons.cache.Cache
import org.beangle.commons.cache.CacheManager
import org.openurp.ws.services.teach.attendance.app.util.DateUtils._
import org.openurp.ws.services.teach.attendance.app.model.ActivityBean
import org.openurp.ws.services.teach.attendance.app.model.Classroom
import java.util.Date
import org.beangle.commons.bean.Initializing

/**
 * 考勤活动服务
 */
class ActivityService extends Logging with Initializing{
  var executor: JdbcExecutor = _
  var cacheManager: CacheManager = _
  var baseDataService: BaseDataService = _
  private var cache: Cache[Long, ActivityBean] = _

  def getActivity(room: Classroom, date: Date): Option[ActivityBean] = {
    var rs: Option[ActivityBean] = None
    
    baseDataService.getSemesterId(date) foreach { semesterId =>
      val datas = executor.query("select aa.id from t_attend_activities  aa " +
        " where aa.semester_id =? and to_char(aa.course_date,'yyyyMMdd')=? and ? between aa.attend_begin_time and aa.end_time and aa.room_id=?",
        semesterId, toDateStr(date), toCourseTime(date), room.id)
      datas.foreach { data =>
        val activityId = data.head.asInstanceOf[Number].longValue
        rs = cache.get(activityId)
        if (rs.isEmpty) {
          val datas = executor.query("select aa.id,aa.lesson_id,aa.course_id,rw.jxbmc class_name,aa.begin_time,aa.end_time "+
              "from t_attend_activities  aa,jxrw_t rw where aa.id=? and aa.lesson_id=rw.id", activityId)
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
            val lesson = new ActivityBean(activityId, course.get, teacherName, className, beginTime, endTime)
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