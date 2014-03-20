package org.openurp.ws.services.teach.attendance.app.impl

import java.util.{ Calendar, Date, Timer, TimerTask }

import org.beangle.commons.bean.Initializing
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.ClassLoaders
import org.beangle.commons.lang.Strings.{ isNotBlank, replace, split }
import org.beangle.commons.logging.Logging
import org.beangle.data.jdbc.query.JdbcExecutor

import org.openurp.ws.services.teach.attendance.app.domain.ShardPolicy.{ detailPostfix, logPostfix }

class ShardDaemon extends TimerTask with Logging with Initializing {
  var executor: JdbcExecutor = _

  def checkDetailTable(date: Calendar) {
    checkAndCreate("t_attend_details", detailPostfix(date))
  }

  def checkLogTable(date: Calendar) {
    checkAndCreate("t_attend_logs", logPostfix(date))
  }

  private def checkAndCreate(table: String, postfix: String) {
    val tableName = table + postfix
    // 1.check
    val count = executor.queryForInt("select count(*) from user_tables where table_name='" + tableName.toUpperCase() + "'")
    // 2.createf
    if (count == 0) {
      val url = ClassLoaders.getResource("ddl/create/" + table + ".sql", this.getClass)
      val sqls = split(IOs.readString(url.openStream()), ";")
      sqls foreach { sql =>
        if (isNotBlank(sql)) {
          val statment = replace(sql.trim, "${postfix}", postfix)
          executor.update(statment)
          logger.info("execute {}", statment)
        }
      }
    }
  }

  /**检查表的时间(每天)*/
  var interval = 24 * (60 * 60 * 1000)
  def run() {
    try {
      var date = Calendar.getInstance()
      val year = date.get(Calendar.YEAR)
      checkDetailTable(date)
      checkLogTable(date)

      //检查下一个月/下一年的表
      date.add(Calendar.MONTH, 1)
      checkDetailTable(date)
      if (date.get(Calendar.YEAR) != year) checkLogTable(date)
    } catch {
      case e: Exception => logger.error("Cannot check and create attend table", e)
    }
  }

  def init() {
    new Timer("Attenance Shard Deamon", true).schedule(this, new Date(), interval)
  }
}