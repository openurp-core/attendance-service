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

import org.beangle.commons.bean.Initializing
import org.beangle.commons.cache.{ Cache, CacheManager }
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.util.DateUtils._
import org.openurp.ws.services.teach.attendance.app.model.CourseBean
import java.sql.Date
/**
 * 基础数据服务
 *
 * @author chaostone
 * @version 1.0, 2014/03/22
 * @since 0.0.1
 */
class BaseDataService extends Initializing {
  var executor: JdbcExecutor = _
  var cacheManager: CacheManager = _

  private var semesterCache: Cache[String, Int] = _
  private var courseCache: Cache[Number, CourseBean] = _
  private var teacherCache: Cache[Number, String] = _
  private var adminclassCache: Cache[Number, String] = _

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

  def getAdminclassName(id: Number): String = {
    var adminclassName = ""
    if (id != null) {
      var rs = adminclassCache.get(id)
      if (rs.isEmpty) {
        val names = executor.query("select a.bjmc from jcxx_bj_t a where a.id=?", id)
        for (name <- names) {
          adminclassName = name.head.toString
          adminclassCache.put(id, adminclassName)
        }
      }
    }
    adminclassName
  }

  def getCourse(id: Number): Option[CourseBean] = {
    var rs = courseCache.get(id)
    if (rs.isEmpty) {
      val names = executor.query("select a.id,a.kcdm,a.kcmc from JCXX_kc_T a where a.id=?", id.longValue())
      for (name <- names) {
        val course = new CourseBean(name(0).asInstanceOf[Number].longValue, name(1).toString, name(2).toString)
        courseCache.put(id, course)
        rs = Some(course)
      }
    }
    rs
  }

  def init() {
    semesterCache = cacheManager.getCache("semester")
    courseCache = cacheManager.getCache("course")
    teacherCache = cacheManager.getCache("teacher")
    adminclassCache = cacheManager.getCache("adminclass")
  }
}