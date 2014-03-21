package org.openurp.ws.services.teach.attendance.app.impl

import java.util.{ Calendar, Date, Timer, TimerTask }
import org.beangle.commons.bean.Initializing
import org.beangle.commons.logging.Logging
import org.beangle.commons.lang.Numbers._
import org.beangle.data.jdbc.query.JdbcExecutor
import org.openurp.ws.services.teach.attendance.app.domain.WeekStates
import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy._
import org.openurp.ws.services.teach.attendance.app.util.DateUtils._
import org.openurp.ws.services.teach.attendance.app.model.AttendType

class DataImporterDaemon extends TimerTask with Initializing {

  var importer: DataImporter = _

  //每天检查
  var interval = 24 * (60 * 60 * 1000)

  //每次同步时间
  var startTime = "15:00"

  def run() {
    val cal = Calendar.getInstance
    importer.importData(Calendar.getInstance)
  }
  def init() {
    val cal = Calendar.getInstance
    cal.set(Calendar.HOUR_OF_DAY, toInt(startTime.substring(0, 2)))
    cal.set(Calendar.MINUTE, toInt(startTime.substring(3)))
    new Timer("Attend Data Importer Deamon", true).schedule(this, cal.getTime(), interval)
  }
}