package org.openurp.ws.services.teach.attendance.app.model

import org.beangle.commons.lang.Objects

class ActivityBean(val id: Long, val course: CourseBean, val teacherName: String, val className: String, val beginTime: Int, val endTime: Int) {

  override def toString(): String =
    Objects.toStringBuilder(this).add("id", id).add("course", course)
      .add("teacherName", teacherName).add("className", className)
      .add("beginTime", beginTime).add("endTime", endTime).toString

  override def hashCode: Int = id.hashCode()
}