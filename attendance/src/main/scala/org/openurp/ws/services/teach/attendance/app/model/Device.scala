package org.openurp.ws.services.teach.attendance.app.model

import java.util.Date
import org.beangle.commons.lang.Objects

class Device(val id: Int, val room: Classroom, var syncAt: Date = null) {
  override def toString: String = {
    Objects.toStringBuilder(this).add("id", id).add("room", room).add("syncAt", syncAt).toString
  }
}

class Classroom(val id: Int, val name: String) {
  override def toString(): String = Objects.toStringBuilder(this).add("id", id).add("name", name).toString
}
