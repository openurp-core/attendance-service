package org.openurp.ws.services.teach.attendance.app.model

import org.beangle.commons.lang.Objects

class CourseBean(val id: Long, val code: String, val name: String) {
  override def toString(): String = Objects.toStringBuilder(this).add("id", id).add("code", code).add("name", name).toString
}