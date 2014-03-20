package org.openurp.ws.services.teach.attendance.app.impl

import java.util.Date
import org.beangle.commons.bean.Initializing
import org.beangle.commons.cache.{ Cache, CacheManager }
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.util.DateUtils._
import org.openurp.ws.services.teach.attendance.app.model.CourseBean
class BaseDataService extends Initializing {
  var executor: JdbcExecutor = _
  var cacheManager: CacheManager = _

  private var semesterCache: Cache[String, Int] = _
  private var courseCache: Cache[Number, CourseBean] = _
  private var teacherCache: Cache[Number, String] = _

  def getSemesterId(date: Date): Option[Int] = {
    val dateStr = toDateStr(date)
    var rs = semesterCache.get(dateStr)
    if (rs.isEmpty) {
      val ids = executor.query("select rl.id from JXRL_T rl where ? between rl.qssj and rl.jzsj", date)
      for (ida <- ids; ido <- ida) {
        val id = ido.asInstanceOf[Number].intValue()
        semesterCache.put(dateStr, id)
        rs = Some(id)
      }
    }
    rs
  }

  def getTeacherName(id: Number): String = {
    var rs = teacherCache.get(id)
    var teacherName = ""
    if (rs.isEmpty) {
      val names = executor.query("select a.xm from JCXX_JZG_T a where a.id=?", id)
      for (name <- names) {
        teacherName = name.head.toString
        teacherCache.put(id, teacherName)
      }
    }
    teacherName
  }

  def getCourse(id: Number): Option[CourseBean] = {
    var rs = courseCache.get(id)
    if (rs.isEmpty) {
      val names = executor.query("select a.id,a.kcdm,a.kcmc from JCXX_kc_T a where a.id=?", id.longValue())
      for (name <- names) {
        val course = new CourseBean(name(0).asInstanceOf[Number].longValue, name(1).toString, name(2).toString)
        courseCache.put(id, course)
        rs =Some(course)
      }
    }
    rs
  }

  def init() {
    semesterCache = cacheManager.getCache("semester")
    courseCache = cacheManager.getCache("course")
    teacherCache = cacheManager.getCache("teacher")
  }
}