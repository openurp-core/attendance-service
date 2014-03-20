package org.openurp.ws.services.teach.attendance.app.model

object AttendType {

  val Unknown = 0
  val Presence = 1
  val Absenteeism = 2
  val Late = 3

  val names = Map((Presence, "出勤"), (Absenteeism, "缺勤"), (Late, "迟到"))
}